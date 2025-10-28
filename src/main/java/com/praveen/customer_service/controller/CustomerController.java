package com.praveen.customer_service.controller;

import com.praveen.customer_service.dto.CustomerInformation;
import com.praveen.customer_service.dto.StockTradeRequest;
import com.praveen.customer_service.dto.StockTradeResponse;
import com.praveen.customer_service.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerInformation> getCustomerInformation(
            @PathVariable("customerId") Integer customerId
    ) {
        return customerService
                .getCustomerInformation(customerId);
    }

    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> trade(
            @PathVariable("customerId") Integer customerId,
            @RequestBody Mono<StockTradeRequest> stockTradeRequest
    ) {
        return customerService
                .trade(customerId, stockTradeRequest);
    }

}
