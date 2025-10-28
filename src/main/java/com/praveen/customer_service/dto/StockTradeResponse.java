package com.praveen.customer_service.dto;

import com.praveen.customer_service.constants.Tickers;
import com.praveen.customer_service.constants.TradeAction;

public record StockTradeResponse(Integer customerId,
                                 Tickers ticker,
                                 Integer price,
                                 Integer quantity,
                                 TradeAction action,
                                 Integer totalPrice,
                                 Integer balance){
}
