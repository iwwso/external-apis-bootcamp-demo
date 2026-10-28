package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Non-blocking client for the external book service.
 * Implemented in the web-client branch; here it only satisfies the bean contract.
 *
 * @author Karyna Litvinova
 */
@Component
public class BookWebClientImpl implements BookWebClient {

    private static final String NOT_IMPLEMENTED = "WebClient is implemented in the web-client branch";

    private final WebClient webClient;

    public BookWebClientImpl(WebClient.Builder builder, ExternalServiceProperties properties) {
        this.webClient = builder
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Override
    public Mono<BookDto> getBookAsync(Long id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Flux<BookDto> getAllBooksAsync() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}