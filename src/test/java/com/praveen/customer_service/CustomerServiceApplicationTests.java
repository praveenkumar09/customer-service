package com.praveen.customer_service;

import com.praveen.customer_service.domain.Tickers;
import com.praveen.customer_service.domain.TradeAction;
import com.praveen.customer_service.dto.StockTradeRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerServiceApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceApplicationTests.class);

    @Autowired
    WebTestClient webTestClient;

    @Test
    void test_customerInformation() {
        this.webTestClient.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10000);
    }

    @Test
    public void test_trade() {
        this.webTestClient.post()
                .uri("/customers/1/trade")
                .bodyValue(new StockTradeRequest(
                        Tickers.APPLE,
                        100,
                        1 ,
                        TradeAction.BUY))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo(1)
                .jsonPath("$.ticker").isEqualTo("APPLE")
                .jsonPath("$.quantity").isEqualTo(1)
                .jsonPath("$.price").isEqualTo(100)
                .jsonPath("$.totalPrice").isEqualTo(100)
                .jsonPath("$.action").isEqualTo("BUY")
                .jsonPath("$.balance").isEqualTo(9900);

        this.webTestClient.post()
                .uri("/customers/1/trade")
                .bodyValue(new StockTradeRequest(
                        Tickers.APPLE,
                        100,
                        1 ,
                        TradeAction.SELL))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo(1)
                .jsonPath("$.ticker").isEqualTo("APPLE")
                .jsonPath("$.quantity").isEqualTo(1)
                .jsonPath("$.price").isEqualTo(100)
                .jsonPath("$.totalPrice").isEqualTo(100)
                .jsonPath("$.action").isEqualTo("SELL")
                .jsonPath("$.balance").isEqualTo(10000);

    }



}
