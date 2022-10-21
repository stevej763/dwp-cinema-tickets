package uk.gov.dwp.uc.pairtest.domain;

import java.util.List;

import static java.util.stream.Collectors.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketOrderFactory {

    public TicketOrder toTicketOrder(List<TicketTypeRequest> ticketTypeRequests) {
        TicketCount adultTicketCount = getTicketCountFor(ticketTypeRequests, ADULT);
        TicketCount childTicketCount = getTicketCountFor(ticketTypeRequests, CHILD);
        TicketCount infantTicketCount = getTicketCountFor(ticketTypeRequests, INFANT);
        return new TicketOrder(adultTicketCount, childTicketCount, infantTicketCount);
    }

    private TicketCount getTicketCountFor(List<TicketTypeRequest> ticketTypeRequests, Type type) {
        List<TicketTypeRequest> filteredRequests = ticketTypeRequests.stream()
                .filter(request -> isTicketTypeOf(type, request))
                .collect(toList());
        long sumOfTickets = filteredRequests.stream()
                .mapToInt(TicketTypeRequest::getNumberOfTickets)
                .summaryStatistics().getSum();
        return new TicketCount(sumOfTickets);
    }

    private boolean isTicketTypeOf(Type type, TicketTypeRequest request) {
        return type.equals(request.getTicketType());
    }
}
