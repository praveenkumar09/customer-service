package com.praveen.customer_service.mapper;

import com.praveen.customer_service.dto.CustomerInformation;
import com.praveen.customer_service.dto.Holding;
import com.praveen.customer_service.entity.Customer;
import com.praveen.customer_service.entity.PortfolioItem;

import java.util.List;

public class EntityDtoMapper {

    public static CustomerInformation toCustomerInformation(
            Customer customer,
            List<PortfolioItem> items
    ) {
        List<Holding> holdings = items.stream()
                .map(item -> new Holding(item.getTicker(), item.getQuantity()))
                .toList();
        return new CustomerInformation(
                customer.getId(),
                customer.getName(),
                customer.getBalance(),
                holdings
        );

    }
}
