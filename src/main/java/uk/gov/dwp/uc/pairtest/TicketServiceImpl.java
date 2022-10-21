package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.stream.Stream;


public class TicketServiceImpl implements TicketService {

    private static final int MAXIMUM_TICKET_BOOKING_ALLOWANCE = 20;
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
        checkRequestHasAtLeastOneValidTicket(ticketTypeRequests);
        checkRequestDoesNotExceedMaximumTicketOrderCount(ticketTypeRequests);

        seatReservationService.reserveSeat(accountId, 0);
        ticketPaymentService.makePayment(accountId, 0);
    }

    private void checkAccountIdIsValid(Long accountId) {
        if (!accountValidator.isValidAccount(accountId)) {
            String message = String.format("Invalid account ID: accountId=%s", accountId);
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkForEmptyArrayOfTicketTypeRequests(TicketTypeRequest[] ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            String message = "Invalid order: Cannot process order due to no TicketTypeRequests being received";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkRequestHasAtLeastOneValidTicket(TicketTypeRequest[] ticketTypeRequests) {
        long totalNumberOfTickets = Stream.of(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).summaryStatistics().getSum();
        if (totalNumberOfTickets < 1) {
            String message = "Invalid order: cannot process order with no tickets";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkRequestDoesNotExceedMaximumTicketOrderCount(TicketTypeRequest[] ticketTypeRequests) {
        long totalNumberOfTickets = Stream.of(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).summaryStatistics().getSum();
        if (totalNumberOfTickets > MAXIMUM_TICKET_BOOKING_ALLOWANCE) {
            String message = String.format("Invalid order: You cannot purchase more than %s tickets in one order", MAXIMUM_TICKET_BOOKING_ALLOWANCE);
            throw new InvalidPurchaseException(message);
        }
    }
}
