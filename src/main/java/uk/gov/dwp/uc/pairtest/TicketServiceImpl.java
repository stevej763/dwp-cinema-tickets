package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;
import java.util.stream.Stream;


public class TicketServiceImpl implements TicketService {

    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;
    private final AccountValidator accountValidator;

    public TicketServiceImpl(SeatReservationService seatReservationService, TicketPaymentService ticketPaymentService, AccountValidator accountValidator) {
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
        this.accountValidator = accountValidator;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountIdIsValid(accountId);
        checkForEmptyArrayOfTicketTypeRequests(ticketTypeRequests);

        long totalNumberOfTickets = Stream.of(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).summaryStatistics().getSum();
        if (totalNumberOfTickets > 20) {
            throw new InvalidPurchaseException("Invalid order: You cannot purchase more than 20 tickets in one order");
        }

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
        if (!accountValidator.isValidAccount(accountId)) {
            String message = String.format("Invalid account ID: accountId=%s", accountId);
            throw new InvalidPurchaseException(message);
        }
    }
}
