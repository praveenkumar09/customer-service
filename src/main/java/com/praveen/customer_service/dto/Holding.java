package com.praveen.customer_service.dto;

import com.praveen.customer_service.domain.Tickers;

public record Holding(Tickers ticker,
                      Integer quantity) {
}
