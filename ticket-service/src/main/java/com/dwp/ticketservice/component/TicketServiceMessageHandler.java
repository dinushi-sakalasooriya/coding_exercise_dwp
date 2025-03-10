package com.dwp.ticketservice.component;

import org.springframework.stereotype.Component;

/**
 * This is the utility class that contains constant error messages related to ticket services.
 *
 * These messages are used throughout the ticket purchasing process to handle various validation errors.
 * The class is designed to be non-instantiable, reusable and easy to maintain the messages related to the ticket service.
 */
@Component
public class TicketServiceMessageHandler {
    public static final String NO_TICKETS_REQUESTED = "No tickets requested";
    public static final String INVALID_ACCOUNT_ID = "Account ID must be greater than zero";
    public static final String MISSING_ADULT_TICKET = "Child or Infant tickets cannot be purchased without purchasing an Adult ticket.";
    public static final String EXCEEDED_TICKET_LIMIT = "Your ticket limit is exceeded at once.";
}
