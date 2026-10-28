package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Non-blocking client for the external book service.
 *
 * @author Karyna Litvinova
 */
@Component
public class BookWebClientImpl implements BookWebClient {

    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;

    public BookWebClientImpl(WebClient.Builder builder, ExternalServiceProperties properties) {
        this.webClient = builder
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Override
    public Mono<BookDto> getBookAsync(Long id) {
        return webClient.get()
                .uri("/books/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new ClientException(
                        "External service returned " + response.statusCode() + " for book " + id)))
                .bodyToMono(BookApiResponse.class)
                .timeout(RESPONSE_TIMEOUT)
                .map(this::toDto)
                .onErrorMap(error -> !(error instanceof ClientException),
                        error -> new ClientException(
                                "Could not fetch book " + id + " from the external service", error));
    }

    @Override
    public Flux<BookDto> getAllBooksAsync() {
        return webClient.get()
                .uri("/books")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new ClientException(
                        "External service returned " + response.statusCode() + " for the book list")))
                .bodyToFlux(BookApiResponse.class)
                .timeout(RESPONSE_TIMEOUT)
                .map(this::toDto)
                .onErrorMap(error -> !(error instanceof ClientException),
                        error -> new ClientException(
                                "Could not fetch the book list from the external service", error));
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {
        return Mono.zip(getBookAsync(id1), getBookAsync(id2))
                .map(both -> List.of(both.getT1(), both.getT2()));
    }

    private BookDto toDto(BookApiResponse response) {
        return new BookDto(response.title(), response.author(), response.genre(), response.price());
    }
}