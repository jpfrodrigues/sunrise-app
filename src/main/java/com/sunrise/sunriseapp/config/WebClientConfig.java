package com.sunrise.sunriseapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sunClient(
            @Value("${app.sun-api.base-url}") String baseUrl,
            @Value("${app.sun-api.timeout-ms}") long timeoutMs
    ) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(timeoutMs));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean
    public WebClient geocodeClient(
            @Value("${app.geocode.base-url}") String baseUrl,
            @Value("${app.geocode.timeout-ms}") long timeoutMs,
            @Value("${app.geocode.user-agent}") String userAgent
    ) {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(timeoutMs));
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", userAgent)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
