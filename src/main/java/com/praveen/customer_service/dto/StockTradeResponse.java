package com.praveen.customer_service.dto;

import com.praveen.customer_service.domain.Tickers;
import com.praveen.customer_service.domain.TradeAction;

public record StockTradeResponse(Integer customerId,
                                 Tickers ticker,
                                 Integer price,
                                 Integer quantity,
                                 TradeAction action,
                                 Integer totalPrice,
                                 Integer balance){
}
