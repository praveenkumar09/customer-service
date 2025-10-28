package com.praveen.customer_service.exception;

public class InsufficientShares extends RuntimeException{
    public InsufficientShares(Integer id) {
        super("Insufficient shares for customer with id " + id);
    }
}
