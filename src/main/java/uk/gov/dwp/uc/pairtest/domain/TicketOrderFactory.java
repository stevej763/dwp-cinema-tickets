package uk.gov.dwp.uc.pairtest.domain;

import java.util.List;

import static java.util.stream.Collectors.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketOrderFactory {

    public TicketOrder toTicketOrder(List<TicketTypeRequest> ticketTypeRequests) {
        TicketCount adultTicketCount = getTicketCountFor(ADULT, ticketTypeRequests);
        TicketCount childTicketCount = getTicketCountFor(CHILD, ticketTypeRequests);
        TicketCount infantTicketCount = getTicketCountFor(INFANT, ticketTypeRequests);
        return new TicketOrder(adultTicketCount, childTicketCount, infantTicketCount);
    }

    private TicketCount getTicketCountFor(Type type, List<TicketTypeRequest> ticketTypeRequests) {
        List<TicketTypeRequest> filteredRequests = filterRequestsForType(type, ticketTypeRequests);
        long sumOfTickets = getSumOfTickets(filteredRequests);
        return new TicketCount(sumOfTickets);
    }

    private List<TicketTypeRequest> filterRequestsForType(Type type, List<TicketTypeRequest> ticketTypeRequests) {
        return ticketTypeRequests.stream()
                .filter(request -> isTicketTypeOf(type, request))
                .collect(toList());
    }

    private long getSumOfTickets(List<TicketTypeRequest> filteredRequests) {
        return filteredRequests.stream()
                .mapToInt(TicketTypeRequest::getNumberOfTickets)
                .summaryStatistics()
                .getSum();
    }

    private boolean isTicketTypeOf(Type type, TicketTypeRequest request) {
        return type.equals(request.getTicketType());
    }
}
