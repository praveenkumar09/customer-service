package com.praveen.customer_service.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(Integer id) {
        super("Customer with id " + id + " not found");
    }
}
