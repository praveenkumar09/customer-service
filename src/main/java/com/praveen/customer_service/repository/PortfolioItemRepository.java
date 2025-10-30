package com.praveen.customer_service.repository;

import com.praveen.customer_service.domain.Tickers;
import com.praveen.customer_service.entity.PortfolioItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PortfolioItemRepository extends ReactiveCrudRepository<PortfolioItem, Integer> {

    Flux<PortfolioItem> findByCustomerId(Integer customerId);

    Mono<PortfolioItem> findByTickerAndCustomerId(Tickers ticker, Integer customerId);
}
