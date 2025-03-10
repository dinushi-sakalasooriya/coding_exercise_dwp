package com.dwp.ticketservice.thirdparty.seatbooking;

import org.springframework.stereotype.Service;

@Service
public interface SeatReservationService {

    void reserveSeat(long accountId, int totalSeatsToAllocate);

}
