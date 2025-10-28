package com.praveen.customer_service.entity;

import org.springframework.data.annotation.Id;

public class Customer {

    @Id
    private Integer id;
    private String name;
    private Integer balance;

    public Customer(Integer id, String name, Integer balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Customer() {
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getBalance() {
        return balance;
    }
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
