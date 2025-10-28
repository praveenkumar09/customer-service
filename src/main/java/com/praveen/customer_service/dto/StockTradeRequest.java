package com.praveen.customer_service.dto;

import com.praveen.customer_service.constants.Tickers;
import com.praveen.customer_service.constants.TradeAction;

public record StockTradeRequest(Tickers ticker,
                                Integer price,
                                Integer quantity,
                                TradeAction action) {
}
