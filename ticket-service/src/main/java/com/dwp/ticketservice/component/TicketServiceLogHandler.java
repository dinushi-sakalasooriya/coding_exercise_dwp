package com.dwp.ticketservice.component;

public class TicketServiceLogHandler {
    public static final String PURCHASING_TICKETS = "Started purchasing tickets for account: {}";
    public static final String STARTED_PURCHASING_TICKETS = "Started purchasing tickets for account: [Account ID hidden]";
    public static final String VALIDATED_TICKET_INPUTS = "Validated ticket inputs, and grouping ticket types and counts into a Map";
    public static final String COMPLETED_GROUPING_TICKET_TYPES = "Completed grouping ticket types, and counting total ticket count";
    public static final String VALIDATING_BUSINESS_RULES = "Started to validate business rules, total ticket count: {}";
    public static final String COMPLETED_VALIDATING_BUSINESS_RULES = "Completed validating business rules, and starting total ticket cost calculation";
    public static final String TOTAL_TICKET_COST = "Total ticket cost calculated: {}";
    public static final String TOTAL_SEAT_COUNT = "Total seat count calculated: {}";
    public static final String INVALID_PERCHES_ERROR = "Error during ticket purchase for account {}: {}";
    public static final String COMMON_ERROR = "Unexpected error occurred while processing ticket purchase";
    public static final String UNEXPECTED_ERROR ="Unexpected error during ticket purchase";
    public static final String START_TEST_CASE ="*************Start the test case name : {}**************";
}

