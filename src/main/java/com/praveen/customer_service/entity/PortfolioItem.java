package com.praveen.customer_service.entity;

import com.praveen.customer_service.constants.Tickers;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


public class PortfolioItem {

    @Id
    private Integer id;
    private Customer customer;
    private Tickers ticker;
    Integer quantity;

    public PortfolioItem(Customer customer, Tickers ticker, Integer quantity) {
        this.customer = customer;
        this.ticker = ticker;
        this.quantity = quantity;
    }

    public PortfolioItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Tickers getTicker() {
        return ticker;
    }

    public void setTicker(Tickers ticker) {
        this.ticker = ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PortfolioItem{" +
                "id=" + id +
                ", customer=" + customer +
                ", ticker=" + ticker +
                ", quantity=" + quantity +
                '}';
    }
}
