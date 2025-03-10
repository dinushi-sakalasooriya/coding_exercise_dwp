package com.dwp.ticketservice.thirdparty.seatbooking;

import org.springframework.stereotype.Service;

@Service
public class SeatReservationServiceImpl implements SeatReservationService {

    @Override
    public void reserveSeat(long accountId, int totalSeatsToAllocate) {
        // Real implementation omitted, assume working code will make the seat reservation.
    }

}
