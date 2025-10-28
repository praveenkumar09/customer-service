package com.praveen.customer_service.advice;

import com.praveen.customer_service.exception.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class CustomerServiceExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleCustomerNotfoundException(
            CustomerNotFoundException ex
    ){
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,
                        ex.getMessage());
        problemDetail.setType(URI
                .create("http://example.com/problems/customer-not-found"));
        problemDetail.setTitle("Customer Not Found");
        return problemDetail;
    }
}
