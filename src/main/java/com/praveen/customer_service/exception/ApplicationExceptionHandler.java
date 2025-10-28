package com.praveen.customer_service.exception;

import reactor.core.publisher.Mono;

public class ApplicationExceptionHandler {

    public static <T>  Mono<T> customerNotFound(Integer id) {
        return Mono.error(new CustomerNotFoundException(id));
    }
}
