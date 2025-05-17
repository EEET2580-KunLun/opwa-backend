package eeet2580.kunlun.opwa.backend.external.pawa.service;

import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.TicketRes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketService {

    /**
     * Retrieves all tickets from PAWA service
     *
     * @return A Flux of TicketDto objects
     */
    Flux<TicketRes> getAllTickets();

    /**
     * Retrieves a specific ticket by ID
     *
     * @param id Ticket ID
     * @return Mono with the ticket if found
     */
    Mono<TicketRes> getTicketById(String id);
}
