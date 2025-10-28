package com.praveen.customer_service.service;

import com.praveen.customer_service.dto.CustomerInformation;
import com.praveen.customer_service.dto.Holding;
import com.praveen.customer_service.dto.StockTradeRequest;
import com.praveen.customer_service.dto.StockTradeResponse;
import com.praveen.customer_service.entity.Customer;
import com.praveen.customer_service.entity.PortfolioItem;
import com.praveen.customer_service.exception.ApplicationExceptionHandler;
import com.praveen.customer_service.repository.CustomerRepository;
import com.praveen.customer_service.repository.PortfolioItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final PortfolioItemRepository portfolioItemRepository;

    @Autowired
    public CustomerService(
            CustomerRepository customerRepository,
            PortfolioItemRepository portfolioItemRepository
    ) {
        this.customerRepository = customerRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer id) {
        return customerRepository.findById(id)
                .switchIfEmpty(ApplicationExceptionHandler.customerNotFound(id))
                .flatMap(this::buildCustomerInformation);

    }

    private Mono<CustomerInformation> buildCustomerInformation(Customer customer) {
        return portfolioItemRepository.findByCustomerId(customer.getId())
                .map(portfolioItem -> new Holding(
                        portfolioItem.getTicker(),
                        portfolioItem.getQuantity()
                ))
                .collectList()
                .map(holdings -> new CustomerInformation(
                        customer.getId(),
                        customer.getName(),
                        customer.getBalance(),
                        holdings
                ));
    }

    public Mono<StockTradeResponse> trade(
            Integer customerId,
            Mono<StockTradeRequest> stockTradeRequest
    ) {
        return Mono.zip(this.customerRepository.findById(customerId)
                                .switchIfEmpty(ApplicationExceptionHandler.customerNotFound(customerId))
                        , stockTradeRequest)
                .flatMap(zip -> decideBuyOrSell(zip.getT1(),zip.getT2()));
    }

    private Mono<StockTradeResponse> decideBuyOrSell(Customer customer, StockTradeRequest req) {
        return switch(req.action()){
            case BUY -> deductAmount(customer,req);
            case SELL -> addAmount(customer,req);
        };
    }

    private Mono<StockTradeResponse> deductAmount(Customer customer, StockTradeRequest req) {
        return Mono.just(customer.getBalance())
                .map(bal -> {
                    int currentPrice = req.price() * req.quantity();
                    if(customer.getBalance() >=  currentPrice){
                        return bal - (req.price() * req.quantity());
                    }else{
                        return 0;
                    }
                })
                .flatMap(bal -> {
                    if (bal > 0) {
                        customer.setBalance(bal);
                        return this.customerRepository.save(customer)
                                .flatMap(latestCustomer -> addQty(customer,req));
                    }
                        return ApplicationExceptionHandler.insufficientBalance(customer.getId());
                });
    }


    private Mono<StockTradeResponse> addAmount(Customer customer, StockTradeRequest req) {
        return Mono.just(customer.getBalance())
                .map( bal -> bal + (req.price() * req.quantity()))
                .flatMap(finalBal -> {
                    customer.setBalance(finalBal);
                    return this.customerRepository.save(customer);
                })
                .flatMap(latestCustomer -> deductQty(latestCustomer,req));

    }

    private Mono<StockTradeResponse> addQty(Customer customer, StockTradeRequest req) {
        return this.portfolioItemRepository
                .findByTickerAndCustomerId(req.ticker(),customer.getId())
                .flatMap(item -> {
                    item.setQuantity(item.getQuantity() + req.quantity());
                    return this.portfolioItemRepository.save(item);
                })
                .switchIfEmpty(Mono.defer(() -> this.portfolioItemRepository.save(
                        new PortfolioItem(
                                customer.getId(),
                                req.ticker(),
                                req.quantity()
                        )
                )))
                .thenReturn(buildTradeResponse(customer,req,req.price() * req.quantity()));
    }

    private Mono<StockTradeResponse> deductQty(Customer customer, StockTradeRequest req) {
        return this
                .portfolioItemRepository
                .findByTickerAndCustomerId(req.ticker(),customer.getId())
                .switchIfEmpty(ApplicationExceptionHandler.insufficientShares(customer.getId()))
                .flatMap(item -> {
                    int totalPrice = req.price() * req.quantity();
                    if(req.quantity() < item.getQuantity()){
                        item.setQuantity(item.getQuantity() - req.quantity());
                        return this.portfolioItemRepository.save(item)
                                .thenReturn(buildTradeResponse(customer,req, totalPrice));
                    }else if(req.quantity().equals(item.getQuantity())){
                        return this.portfolioItemRepository.deleteById(item.getId())
                                .thenReturn(buildTradeResponse(customer,req, totalPrice));
                    }
                    return ApplicationExceptionHandler.insufficientShares(customer.getId());
                });
    }

    private StockTradeResponse buildTradeResponse(Customer customer, StockTradeRequest request, Integer totalPrice) {
        return new StockTradeResponse(
                customer.getId(),
                request.ticker(),
                request.price(),
                request.quantity(),
                request.action(),
                totalPrice,
                customer.getBalance()
        );
    }


}
