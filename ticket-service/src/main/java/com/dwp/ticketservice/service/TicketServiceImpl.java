package com.dwp.ticketservice.service;


import com.dwp.ticketservice.TicketEnum.TicketInfo;
import com.dwp.ticketservice.exception.InvalidPurchaseException;
import com.dwp.ticketservice.thirdparty.seatbooking.SeatReservationService;
import com.dwp.ticketservice.domain.TicketTypeRequest;
import com.dwp.ticketservice.thirdparty.paymentgateway.TicketPaymentService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static com.dwp.ticketservice.component.TicketServiceMessageHandler.*;
import static com.dwp.ticketservice.component.TicketServiceLogHandler.*;
import static com.dwp.ticketservice.domain.TicketTypeRequest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    /**
     * This is focus on payment and reservation construct for make payment and do the reservation.
     *
     * @param paymentService
     * @param reservationService
     */
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        try{
            logger.info(STARTED_PURCHASING_TICKETS);
            logger.debug(PURCHASING_TICKETS, accountId);
            validateTicketRequestParameters(ticketTypeRequests,accountId);

            logger.info(VALIDATED_TICKET_INPUTS);
            Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = groupingTicketCountByType(ticketTypeRequests);

            logger.info(COMPLETED_GROUPING_TICKET_TYPES);
            int totalTicketCount = calculateTotalTicketCount(ticketTypeCountMap);

            logger.info(VALIDATING_BUSINESS_RULES, totalTicketCount);
            validateBusinessRules(ticketTypeCountMap, totalTicketCount);
            logger.info(COMPLETED_VALIDATING_BUSINESS_RULES);

            int totalTicketCost = calculateTotalTicketCost(ticketTypeCountMap);
            logger.info(TOTAL_TICKET_COST, totalTicketCost);
            int totalSeatCount = calculateTotalSeatCount(ticketTypeCountMap,totalTicketCount);
            logger.info(TOTAL_SEAT_COUNT, totalSeatCount);

            makePayment(accountId,totalTicketCost);
            reserveSeat(accountId, totalSeatCount);
        } catch (InvalidPurchaseException e) {
            logger.error(INVALID_PERCHES_ERROR, accountId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(COMMON_ERROR +e.getLocalizedMessage(), accountId, e);
            throw new RuntimeException(UNEXPECTED_ERROR, e);
        }
    }

    private int calculateTotalTicketCount(Map<Type, Integer> ticketTypeCountMap) {
        int totalTicketCost = 0;
        if (ticketTypeCountMap != null){
            totalTicketCost = ticketTypeCountMap.values().stream().mapToInt(Integer::intValue).sum();
        }
        return totalTicketCost;
    }

    private int calculateTotalSeatCount(Map<Type, Integer> ticketTypeCountMap, int totalTicketCount) {
        return totalTicketCount - ticketTypeCountMap.getOrDefault(Type.infant, 0);
    }

    private int calculateTotalTicketCost(Map<Type, Integer> ticketTypeCountMap) {
        int adultCost = TicketInfo.ADULT_PRICE.getInfo()  * ticketTypeCountMap.getOrDefault(Type.adult, 0);
        int childCost = TicketInfo.CHILD_PRICE.getInfo() * ticketTypeCountMap.getOrDefault(Type.child, 0);
        return adultCost + childCost; // I leave like this due to readability, otherwise this also can be write as one line.
    }

    private void validateTicketRequestParameters(TicketTypeRequest[] ticketTypeRequests, Long accountId) {
        if(Objects.isNull(ticketTypeRequests) ||ticketTypeRequests.length ==0){
            logger.warn(NO_TICKETS_REQUESTED);
            throw new InvalidPurchaseException(NO_TICKETS_REQUESTED);
        }
        if(accountId<=0){
            logger.warn(INVALID_ACCOUNT_ID);
            throw new InvalidPurchaseException(INVALID_ACCOUNT_ID);
        }
    }

    private Map<Type,Integer> groupingTicketCountByType(TicketTypeRequest[] ticketTypeRequests) {
        Map<TicketTypeRequest.Type, Integer> ticketTypeCounts = new HashMap<>();
        for(TicketTypeRequest request :ticketTypeRequests){
            ticketTypeCounts.put(request.getTicketType(), ticketTypeCounts.getOrDefault(request.getTicketType(), 0) + request.getNoOfTickets());
        }
        return ticketTypeCounts;
    }

    private void validateBusinessRules(Map<Type, Integer> ticketTypeCounts, int totalTicketCount) {
        boolean isContainAdultTicket = ticketTypeCounts.containsKey(Type.adult);//ticketTypeCounts.keySet().stream().anyMatch(key -> key == Type.adult);
        if (totalTicketCount > TicketInfo.MAX_TICKET_COUNT.getInfo()) {
            logger.warn(EXCEEDED_TICKET_LIMIT,TicketInfo.MAX_TICKET_COUNT.getInfo());
            throw new InvalidPurchaseException(EXCEEDED_TICKET_LIMIT);
        }
        if(!isContainAdultTicket){
            logger.warn(MISSING_ADULT_TICKET);
            throw new InvalidPurchaseException(MISSING_ADULT_TICKET);
        }
    }

    private void makePayment(Long accountId, int totalCost) {
        paymentService.makePayment(accountId,totalCost);
    }

    private void reserveSeat(Long accountId, int totalSeatCount) {
        reservationService.reserveSeat(accountId,totalSeatCount);
    }
}
