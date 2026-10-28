package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Blocking client for the external book service.
 *
 * @author Karyna Litvinova
 */
@Component
public class BookRestClientImpl implements BookRestClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);

            if (response == null) {
                throw new ClientException("External service returned an empty body for book " + id);
            }
            return toDto(response);

        } catch (HttpClientErrorException ex) {
            throw new ClientException("External service rejected the request for book " + id
                    + ": " + ex.getStatusCode(), ex);
        } catch (HttpServerErrorException ex) {
            throw new ClientException("External service failed while returning book " + id
                    + ": " + ex.getStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            throw new ClientException("External service is unreachable or too slow for book " + id, ex);
        } catch (RestClientException ex) {
            throw new ClientException("Unreadable response from external service for book " + id, ex);
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] response = restClient.get()
                    .uri("/books")
                    .retrieve()
                    .body(BookApiResponse[].class);

            if (response == null) {
                throw new ClientException("External service returned an empty body for the book list");
            }
            return Arrays.stream(response)
                    .map(this::toDto)
                    .toList();

        } catch (HttpClientErrorException ex) {
            throw new ClientException("External service rejected the request for all books: "
                    + ex.getStatusCode(), ex);
        } catch (HttpServerErrorException ex) {
            throw new ClientException("External service failed while returning all books: "
                    + ex.getStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            throw new ClientException("External service is unreachable or too slow for all books", ex);
        } catch (RestClientException ex) {
            throw new ClientException("Unreadable response from external service for all books", ex);
        }
    }

    private BookDto toDto(BookApiResponse response) {
        return new BookDto(response.title(), response.author(), response.genre(), response.price());
    }
}