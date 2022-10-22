package uk.gov.dwp.uc.pairtest.services;

import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketOrderFactory;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;


public class TicketRequestProcessor {

    private static final long MAXIMUM_TICKET_BOOKING_ALLOWANCE = 20L;
    private static final long MINIMUM_TICKET_COUNT = 1L;
    private static final long MINIMUM_ADULT_TICKET_COUNT = 1L;

    private final TicketOrderFactory ticketOrderFactory;

    public TicketRequestProcessor(TicketOrderFactory ticketOrderFactory) {
        this.ticketOrderFactory = ticketOrderFactory;
    }

    public TicketOrder createValidTicketOrder(List<TicketTypeRequest> request) throws InvalidPurchaseException {
        TicketOrder ticketOrder = ticketOrderFactory.toTicketOrder(request);
        checkRequestHasValidTicketCount(ticketOrder);
        checkThereIsAtLeastOneAdultForEveryInfant(ticketOrder);
        return ticketOrder;
    }

    private void checkRequestHasValidTicketCount(TicketOrder ticketOrder) {
        hasMinimumOfOneTicket(ticketOrder);
        hasAtLeastOneAdultTicketInTheOrder(ticketOrder);
        checkRequestDoesNotExceedMaximumTicketOrderCount(ticketOrder);

    }

    private void hasMinimumOfOneTicket(TicketOrder ticketOrder) {
        if (ticketOrder.getTotalTicketCount() < MINIMUM_TICKET_COUNT) {
            String message = "Invalid order: cannot process order with no tickets";
            throw new InvalidPurchaseException(message);
        }
    }

    private void hasAtLeastOneAdultTicketInTheOrder(TicketOrder ticketOrder) {
        if (ticketOrder.getAdultTicketCountAsLong() < MINIMUM_ADULT_TICKET_COUNT) {
            String message = "Invalid order: You must order at least one adult ticket";
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkRequestDoesNotExceedMaximumTicketOrderCount(TicketOrder ticketOrder) {
        if (ticketOrder.getTotalTicketCount() > MAXIMUM_TICKET_BOOKING_ALLOWANCE) {
            String message = String.format("Invalid order: You cannot purchase more than %s tickets in one order", MAXIMUM_TICKET_BOOKING_ALLOWANCE);
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkThereIsAtLeastOneAdultForEveryInfant(TicketOrder ticketOrder) {
        if(ticketOrder.getInfantTicketCountAsLong() > ticketOrder.getAdultTicketCountAsLong()) {
            String message = "Invalid order: there must be at least one adult for every infant";
            throw new InvalidPurchaseException(message);
        }
    }
}
