package eeet2580.kunlun.opwa.backend.external.pawa.service.impl;

import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.TicketRes;
import eeet2580.kunlun.opwa.backend.external.pawa.service.TicketService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final WebClient pawaWebClient;

    @Override
    @CircuitBreaker(name = "ticketService", fallbackMethod = "getAllTicketsFallback")
    public Flux<TicketRes> getAllTickets() {
        return pawaWebClient.get()
                .uri("/api/tickets/")
                .retrieve()
                .bodyToFlux(TicketRes.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.error("Error fetching tickets from PAWA service", e));
    }

    @Override
    @CircuitBreaker(name = "ticketService", fallbackMethod = "getTicketByIdFallback")
    public Mono<TicketRes> getTicketById(String id) {
        return pawaWebClient.get()
                .uri("/api/tickets/{id}", id)
                .retrieve()
                .bodyToMono(TicketRes.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.error("Error fetching ticket with ID {} from PAWA service", id, e));
    }

    // Fallback methods
    public Flux<TicketRes> getAllTicketsFallback(Exception e) {
        log.warn("Using fallback for getAllTickets due to: {}", e.getMessage());
        return Flux.empty();
    }

    public Mono<TicketRes> getTicketByIdFallback(String id, Exception e) {
        log.warn("Using fallback for getTicketById({}) due to: {}", id, e.getMessage());
        return Mono.empty();
    }
}
