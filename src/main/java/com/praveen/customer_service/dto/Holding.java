package com.praveen.customer_service.dto;

import com.praveen.customer_service.constants.Tickers;

public record Holding(Tickers ticker,
                      Integer quantity) {
}
