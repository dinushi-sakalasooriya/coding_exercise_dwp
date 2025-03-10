package com.dwp.ticketservice.domain;

/**
 * Immutable Object
 */
public class TicketTypeRequest
{

    private int noOfTickets;
    private Type type;

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        adult, child , infant
    }
    public TicketTypeRequest(Type type, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.type = type;
        this.noOfTickets = quantity;
    }

}
