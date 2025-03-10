package com.dwp.ticketservice.thirdparty.paymentgateway;

import org.springframework.stereotype.Service;

@Service
public class TicketPaymentServiceImpl implements TicketPaymentService {

    @Override
    public void makePayment(long accountId, int totalAmountToPay) {
        // Real implementation omitted, assume working code will take the payment using a card pre linked to the account.
    }

}
