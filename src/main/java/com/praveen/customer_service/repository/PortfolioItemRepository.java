package com.praveen.customer_service.repository;

import com.praveen.customer_service.entity.Customer;
import com.praveen.customer_service.entity.PortfolioItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PortfolioItemRepository extends ReactiveCrudRepository<PortfolioItem, Integer> {

    Flux<PortfolioItem> findByCustomerId(Integer customerId);

}
