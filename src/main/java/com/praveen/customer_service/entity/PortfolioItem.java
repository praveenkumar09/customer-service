package com.praveen.customer_service.entity;

import com.praveen.customer_service.domain.Tickers;
import org.springframework.data.annotation.Id;


public class PortfolioItem {

    @Id
    private Integer id;
    private Integer customerId;
    private Tickers ticker;
    Integer quantity;

    public PortfolioItem(Integer customerId, Tickers ticker, Integer quantity) {
        this.customerId = customerId;
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

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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
                ", customerId=" + customerId +
                ", ticker=" + ticker +
                ", quantity=" + quantity +
                '}';
    }
}
