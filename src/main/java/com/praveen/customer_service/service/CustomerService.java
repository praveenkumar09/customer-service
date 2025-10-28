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


    public Mono<StockTradeResponse> trade(Integer customerId, Mono<StockTradeRequest> stockTradeRequestMono) {
        return Mono.zip(
                        customerRepository.findById(customerId)
                                .switchIfEmpty(ApplicationExceptionHandler.customerNotFound(customerId)),
                        stockTradeRequestMono
                )
                .flatMap(tuple -> {
                    Customer customer = tuple.getT1();
                    StockTradeRequest request = tuple.getT2();
                    return processTrade(customer, request);
                });
    }

    private Mono<StockTradeResponse> processTrade(Customer customer, StockTradeRequest request) {
        return switch (request.action()) {
            case BUY -> processBuyTrade(customer, request);
            case SELL -> processSellTrade(customer, request);
        };
    }

    private Mono<StockTradeResponse> processBuyTrade(Customer customer, StockTradeRequest request) {
        Integer totalCost = request.quantity() * request.price();

        if (customer.getBalance() < totalCost) {
            return ApplicationExceptionHandler.insufficientBalance(customer.getId());
        }

        customer.setBalance(customer.getBalance() - totalCost);

        return customerRepository.save(customer)
                .flatMap(savedCustomer -> updatePortfolioForBuy(savedCustomer, request))
                .map(savedCustomer -> buildTradeResponse(savedCustomer, request, totalCost));
    }

    private Mono<StockTradeResponse> processSellTrade(Customer customer, StockTradeRequest request) {
        return verifyPortfolioHoldings(customer.getId(), request)
                .flatMap(portfolioItem -> {
                    Integer totalRevenue = request.quantity() * request.price();
                    customer.setBalance(customer.getBalance() + totalRevenue);

                    return customerRepository.save(customer)
                            .flatMap(savedCustomer -> updatePortfolioForSell(savedCustomer, request, portfolioItem))
                            .map(savedCustomer -> buildTradeResponse(savedCustomer, request, totalRevenue));
                });
    }

    private Mono<Customer> updatePortfolioForBuy(Customer customer, StockTradeRequest request) {
        return portfolioItemRepository.findByCustomerId(customer.getId())
                .filter(item -> item.getTicker().equals(request.ticker()))
                .next()
                .flatMap(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
                    return portfolioItemRepository.save(existingItem);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    PortfolioItem newItem = new PortfolioItem(
                            customer.getId(),
                            request.ticker(),
                            request.quantity()
                    );
                    return portfolioItemRepository.save(newItem);
                }))
                .thenReturn(customer);
    }

    private Mono<Customer> updatePortfolioForSell(Customer customer, StockTradeRequest request, PortfolioItem portfolioItem) {
        int newQuantity = portfolioItem.getQuantity() - request.quantity();

        if (newQuantity == 0) {
            return portfolioItemRepository.delete(portfolioItem)
                    .thenReturn(customer);
        }

        portfolioItem.setQuantity(newQuantity);
        return portfolioItemRepository.save(portfolioItem)
                .thenReturn(customer);
    }


    private Mono<PortfolioItem> verifyPortfolioHoldings(Integer customerId, StockTradeRequest request) {
        return portfolioItemRepository.findByCustomerId(customerId)
                .filter(item -> item.getTicker().equals(request.ticker()))
                .filter(item -> item.getQuantity() >= request.quantity())
                .next()
                .switchIfEmpty(ApplicationExceptionHandler.insufficientShares(customerId));
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
