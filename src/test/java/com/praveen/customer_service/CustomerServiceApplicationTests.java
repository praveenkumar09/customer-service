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
    void test_customerInformation_customerNotFoundError() {
        this.webTestClient.get()
                .uri("/customers/100")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.type").isEqualTo("http://example.com/problems/customer-not-found")
                .jsonPath("$.title").isEqualTo("Customer Not Found")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("Customer with id 100 not found")
                .jsonPath("$.instance").isNotEmpty();
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

    @Test
    public void test_trade_InsufficientShares(){
        this.webTestClient.post()
                .uri("/customers/1/trade")
                .bodyValue(new StockTradeRequest(
                        Tickers.APPLE,
                        100,
                        1 ,
                        TradeAction.SELL))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.type").isEqualTo("http://example.com/problems/insufficient-shares")
                .jsonPath("$.title").isEqualTo("Insufficient Shares")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("Insufficient shares for customer with id 1")
                .jsonPath("$.instance").isNotEmpty();

    }


    @Test
    public void test_trade_InsufficientBalance(){
        this.webTestClient.post()
                .uri("/customers/1/trade")
                .bodyValue(new StockTradeRequest(
                        Tickers.APPLE,
                        100000,
                        1 ,
                        TradeAction.BUY))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.type").isEqualTo("http://example.com/problems/insufficient-balance")
                .jsonPath("$.title").isEqualTo("Insufficient Balance")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("Insufficient balance for customer with id 1")
                .jsonPath("$.instance").isNotEmpty();

    }



}
