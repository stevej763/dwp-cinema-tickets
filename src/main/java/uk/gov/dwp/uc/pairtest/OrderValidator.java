package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketOrderFactory;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;


public class OrderValidator {

    private static final int MAXIMUM_TICKET_BOOKING_ALLOWANCE = 20;
    private final TicketOrderFactory ticketOrderFactory;

    public OrderValidator(TicketOrderFactory ticketOrderFactory) {
        this.ticketOrderFactory = ticketOrderFactory;
    }

    public TicketOrder checkForValidTicketOrder(List<TicketTypeRequest> request) throws InvalidPurchaseException {
        TicketOrder ticketOrder = ticketOrderFactory.toTicketOrder(request);
        checkRequestHasAtLeastOneValidTicket(ticketOrder.getTotalTicketCount());
        checkForAtLeastOneAdultTicket(ticketOrder);
        checkRequestDoesNotExceedMaximumTicketOrderCount(ticketOrder.getTotalTicketCount());
        checkThereIsAtLeastOneAdultForEveryInfant(ticketOrder);
        return ticketOrder;
    }

    private void checkThereIsAtLeastOneAdultForEveryInfant(TicketOrder ticketOrder) {
        if(ticketOrder.getInfantTicketCountAsLong() > ticketOrder.getAdultTicketCountAsLong()) {
            String message = "Invalid order: there must be at least one adult for every infant";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkForAtLeastOneAdultTicket(TicketOrder ticketOrder) {
        if (ticketOrder.getAdultTicketCountAsLong() < 1) {
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
