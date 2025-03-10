package com.dwp.ticketservice.service;

import com.dwp.ticketservice.exception.InvalidPurchaseException;
import com.dwp.ticketservice.domain.TicketTypeRequest;
import org.springframework.stereotype.Service;

/**
 * This is Service interface for handling ticket purchase operations.
 *
 * This interface defines the method for purchasing tickets to process the business rules,
 * payment, and seat reservation including the validating the constraint, rules and input of the ticket services.
 */
@Service
public interface TicketService {

    /**
     * Processes the tickets based on the provided ticket requests.
     *
     * It validates the ticket purchase request according to business rules related to
     * the ticket limits, type of tickets, and seat reservation.
     *
     * Throws an InvalidPurchaseException if the purchase request is invalid.
     *
     * @param accountId the account ID of the user purchasing the tickets
     * @param ticketRequests the array of ticket request (Adult, Child, Infant)
     * @throws InvalidPurchaseException if the purchase request violates business rules.
     */
    void purchaseTickets(Long accountId, TicketTypeRequest... ticketRequests) throws InvalidPurchaseException;

}

