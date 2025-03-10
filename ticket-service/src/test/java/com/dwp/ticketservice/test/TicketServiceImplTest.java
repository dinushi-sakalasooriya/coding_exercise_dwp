package com.dwp.ticketservice.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.dwp.ticketservice.component.TicketServiceMessageHandler;
import com.dwp.ticketservice.domain.TicketTypeRequest;
import com.dwp.ticketservice.exception.InvalidPurchaseException;
import com.dwp.ticketservice.service.TicketServiceImpl;
import com.dwp.ticketservice.thirdparty.paymentgateway.TicketPaymentService;
import com.dwp.ticketservice.thirdparty.seatbooking.SeatReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;

/**
 * Unit tests for the testing ticket purchasing functionality.
 * The tests consist validating input parameter, business rules, calculating total price, ensuring the max ticket limit,
 * and handling invalid inputs such as missing adult tickets or invalid accounts.
 */
class TicketServiceImplTest {

    private TicketServiceImpl ticketService;
    private TicketPaymentService paymentService;
    private SeatReservationService reservationService;
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImplTest.class);

    @BeforeEach
    void setUp() {
        paymentService = Mockito.mock(TicketPaymentService.class);
        reservationService = Mockito.mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    /**
     * Test case for purchasing valid tickets, verifying the payment and seat reservation.
     * Verifies that payment is made correctly below details.
     *
     * 1 adult ticket and 2 child tickets including the total payment of 45 and seat allocation of 3
     * 25 for  1 adult and 20 for  2 children, total 45 no of amount
     * 3 total seats (1 adult + 2 children)
     */
    @Test
    void testPurchaseValidTickets(TestInfo testInfo) {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 1);
        TicketTypeRequest childTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.child, 2);
        ticketService.purchaseTickets(1L, adultTicket, childTicket);
        verifyPayment(1L,45,3);
    }

    /**
     * Test case for verify that tickets cannot be purchased without an adult ticket.
     */
    @Test
    void testPurchaseTicketsWithoutAdult() {
        TicketTypeRequest childTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.child, 2);
        TicketTypeRequest infantTicket =ticketTypeRequestObjectCreation(TicketTypeRequest.Type.infant, 1);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, childTicket, infantTicket));
        Assertions.assertEquals(TicketServiceMessageHandler.MISSING_ADULT_TICKET, exception.getMessage());
    }

    /**
     * Test case for checking attempt to purchase more than the max allowed tickets (25)
     * throws an InvalidPurchaseException.
     */
    @Test
    void testPurchaseExceedingMaxTickets() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 27);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, adultTicket));
        assertEquals(TicketServiceMessageHandler.EXCEEDED_TICKET_LIMIT, exception.getMessage());
    }
    /**
     * Test case for the checking an InvalidPurchaseException is thrown if no tickets are requested.
     */
    @Test
    void testPurchaseNoTickets() {
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L));
        assertEquals(TicketServiceMessageHandler.NO_TICKETS_REQUESTED, exception.getMessage());
    }
    /**
     * Test case for validating account ID is provided.
     */
    @Test
    void testPurchaseInvalidAccountId() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 1);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(-1L, adultTicket));
        assertEquals(TicketServiceMessageHandler.INVALID_ACCOUNT_ID, exception.getMessage());
    }
    /**
     * Test case for checking the tickets can be purchased with infants.     *
     * Business rules is, Infants are not charged and do not require a reserved seat.
     *
     * cost = 1 adult * 25 = 25
     * seat = 1 adult + no seats for infant
     */
    @Test
    void testPurchaseTicketsWithInfants() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 1);
        TicketTypeRequest infantTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.infant, 2);
        ticketService.purchaseTickets(1L, adultTicket, infantTicket);
        verifyPayment(1L,25,1);
    }
    /**
     * Test case for checking the tickets count and seats adult, children with infants.     *
     * Here verify the total ticket count and seat allocation correctly.
     *
     * cost = 1 adult * 25 + 2 child * 10 = 45 (no charge for infant)
     * seat = 1 adult + 2 child + no seats for infant
     */
    @Test
    void testPurchaseValidTicketCount() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 1);
        TicketTypeRequest childTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.child, 2);
        TicketTypeRequest infantTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.infant, 2);
        ticketService.purchaseTickets(1L, adultTicket, childTicket,infantTicket);
        verifyPayment(1L,45,3);
    }
    /**
     * Test case for checking the tickets price calculation and seats adult, and children without infants.     *
     * Here verify the total ticket cost and seat allocation correctly.
     *
     * Requesting 2 adult ticket and 3 child ticket
     * cost = 2 adult * 25 + 3 child * 10 = 80 (no charge for infant)
     * seat = 2 adult + 3 child
     */
    @Test
    void testPurchaseTicketsTotalPriceCalculation() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 2);
        TicketTypeRequest childTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.child, 3);
        ticketService.purchaseTickets(1L, adultTicket, childTicket);
        verifyPayment(1L,80,5);
    }
    /**
     * Test case for checking the zero ticket requesting scenario.
     */
    @Test
    void testZeroTicketCount() {
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L));
        assertEquals(TicketServiceMessageHandler.NO_TICKETS_REQUESTED, exception.getMessage());
    }
    /**
     * Test case for checking the max ticket limit exceeding scenario.
     *
     * Request tickets more than mzx limit of tickets
     */
    @Test
    void testExceedingMaxTickets() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 26);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, adultTicket));
        assertEquals(TicketServiceMessageHandler.EXCEEDED_TICKET_LIMIT, exception.getMessage());
    }
    /**
     * Test case for checking the correct price and seal allocation when requesting the same type tickets.
     *
     * Requesting 3 adult tickets
     *  3 Adults * 25 = 75
     *  seat allocation = 3
     */
    @Test
    void testCorrectPriceWhenMultipleAdultTickets() {
        // Requesting 3 adult tickets
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 3);
        ticketService.purchaseTickets(1L, adultTicket);
        verifyPayment(1L,75,3);
    }
    /**
     * Test case for checking the Requesting 1 adult ticket, 2 child tickets, and 3 infant tickets.
     *
     * Total Cost = 1 Adult* 25 + 2 child * 10 = 45
     * Seat Allocation = 3
     */
    @Test
    void testCorrectPriceWithMultipleTypesIncludingInfants() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 1);
        TicketTypeRequest childTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.child, 2);
        TicketTypeRequest infantTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.infant, 3);
        ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);
        verifyPayment(1L,45,3);
    }
    /**
     * Test case for checking the Requesting 1 adult ticket, 1 child tickets, and 1 infant tickets.
     *
     * Total Cost = 1 Adult* 25 + 1 child * 10 = 35
     * Seat Allocation = 2
     */
    @Test
    void testCorrectTicketCountWithMultipleTicketTypes() {
        TicketTypeRequest adultTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.adult, 1);
        TicketTypeRequest childTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.child, 1);
        TicketTypeRequest infantTicket = ticketTypeRequestObjectCreation(TicketTypeRequest.Type.infant, 1);
        ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);
        verifyPayment(1L,35,2);
    }

    /**
     * Verifies the payment service and seat reservation service with the correct parameters.
     *
     * @param accountId the account ID for which payment and seat reservation are made
     * @param totalAmountToPay the total amount to be paid
     * @param totalSeatAllocate the total number of seats to be reserved
     */
    private void verifyPayment(long accountId, int totalAmountToPay, int totalSeatAllocate) {
        verify(paymentService).makePayment(accountId, totalAmountToPay);
        verify(reservationService).reserveSeat(accountId, totalSeatAllocate);
    }

    /**
     * Create a TicketTypeRequest object with the specified type and quantity for reuse
     *
     * @param type the type of the ticket (adult, child, or infant)
     * @param quantity the quantity of tickets requested
     * @return a TicketTypeRequest object with the specified type and quantity
     */
    private TicketTypeRequest ticketTypeRequestObjectCreation(TicketTypeRequest.Type type, int quantity) {
        return new TicketTypeRequest(type, quantity);
    }
}
