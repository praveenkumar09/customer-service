package com.praveen.customer_service.exception;

public class InsufficientBalance extends RuntimeException{
    public InsufficientBalance(Integer id) {
        super("Insufficient balance for customer with id " + id);
    }
}
