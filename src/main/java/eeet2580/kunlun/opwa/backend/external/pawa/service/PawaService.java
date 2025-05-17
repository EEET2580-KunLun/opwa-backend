package eeet2580.kunlun.opwa.backend.external.pawa.service;

import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.PassengerRes;
import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.TicketRes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PawaService {

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

    /**
     * Retrieves all passengers from PAWA service
     *
     * @return A Flux of PassengerDto objects
     */
    Flux<PassengerRes> getAllPassengers();

    /**
     * Retrieves a specific passenger by ID
     *
     * @param id Passenger ID
     * @return Mono with the passenger if found
     */
    Mono<PassengerRes> getPassengerById(String id);
}
