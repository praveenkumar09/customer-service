package com.praveen.customer_service.service;

import com.praveen.customer_service.dto.CustomerInformation;
import com.praveen.customer_service.dto.Holding;
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
                .flatMap(customer -> portfolioItemRepository.findByCustomerId(customer.getId())
                        .map(portfolioItem -> new Holding(
                                portfolioItem.getTicker(),
                                portfolioItem.getQuantity()
                        )
                        )
                        .collectList()
                        .map(holdings-> new CustomerInformation(
                               customer.getId(),
                                customer.getName(),
                                customer.getBalance(),
                                holdings
                        )));

    }
}
