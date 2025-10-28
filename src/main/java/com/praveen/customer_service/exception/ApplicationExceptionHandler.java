package com.praveen.customer_service.exception;

import reactor.core.publisher.Mono;

public class ApplicationExceptionHandler {

    public static <T>  Mono<T> customerNotFound(Integer id) {
        return Mono.error(new CustomerNotFoundException(id));
    }

    public static <T>  Mono<T> insufficientShares(Integer id) {
        return Mono.error(new InsufficientShares(id));
    }

    public static <T>  Mono<T> insufficientBalance(Integer id) {
        return Mono.error(new InsufficientBalance(id));
    }
}
