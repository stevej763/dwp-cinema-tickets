package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;


public class TicketServiceImpl implements TicketService {

    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;

    public TicketServiceImpl(SeatReservationService seatReservationService, TicketPaymentService ticketPaymentService) {
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountIdIsValid(accountId);
        checkForEmptyArrayOfTicketTypeRequests(ticketTypeRequests);

        seatReservationService.reserveSeat(accountId, 0);
        ticketPaymentService.makePayment(accountId, 0);
    }

    private void checkForEmptyArrayOfTicketTypeRequests(TicketTypeRequest[] ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            String message = "Cannot process order due to no TicketTypeRequests being received";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkAccountIdIsValid(Long accountId) {
        if (accountId == null || accountId <= 0) {
            String message = String.format("Invalid account ID: accountId=%s", accountId);
            throw new InvalidPurchaseException(message);
        }
    }
}
