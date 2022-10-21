package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketOrderFactory;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;


public class TicketServiceImpl implements TicketService {

    private static final int MAXIMUM_TICKET_BOOKING_ALLOWANCE = 20;

    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;
    private final AccountValidator accountValidator;
    private final TicketOrderFactory ticketOrderFactory;

    public TicketServiceImpl(
            SeatReservationService seatReservationService,
            TicketPaymentService ticketPaymentService,
            AccountValidator accountValidator,
            TicketOrderFactory ticketOrderFactory) {
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
        this.accountValidator = accountValidator;
        this.ticketOrderFactory = ticketOrderFactory;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountIdIsValid(accountId);
        checkForEmptyArrayOfTicketTypeRequests(ticketTypeRequests);
        checkForValidTicketOrder(ticketTypeRequests);

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

    private void checkForValidTicketOrder(TicketTypeRequest[] ticketTypeRequests) {
        TicketOrder ticketOrder = ticketOrderFactory.toTicketOrder(List.of(ticketTypeRequests));
        checkRequestHasAtLeastOneValidTicket(ticketOrder.getTotalTicketCount());
        checkRequestDoesNotExceedMaximumTicketOrderCount(ticketOrder.getTotalTicketCount());
        if (ticketOrder.getAdultTicketCount() < 1) {
            String message = "Invalid order: You must order at least one adult ticket";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkRequestHasAtLeastOneValidTicket(long totalNumberOfTickets) {
        if (totalNumberOfTickets < 1) {
            String message = "Invalid order: cannot process order with no tickets";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkRequestDoesNotExceedMaximumTicketOrderCount(long totalNumberOfTickets) {
        if (totalNumberOfTickets > MAXIMUM_TICKET_BOOKING_ALLOWANCE) {
            String message = String.format("Invalid order: You cannot purchase more than %s tickets in one order", MAXIMUM_TICKET_BOOKING_ALLOWANCE);
            throw new InvalidPurchaseException(message);
        }
    }
}
