package com.dwp.ticketservice.TicketEnum;

public enum TicketInfo {
    INFANT_PRICE(0),
    CHILD_PRICE(10),
    ADULT_PRICE(25),
    MAX_TICKET_COUNT(25);

    private final int value;

    TicketInfo(int value) {
        this.value = value;
    }

    public int getInfo() {
        return value;
    }
}