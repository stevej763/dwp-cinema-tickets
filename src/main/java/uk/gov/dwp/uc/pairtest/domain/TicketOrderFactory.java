package uk.gov.dwp.uc.pairtest.domain;

import java.util.List;

import static java.util.stream.Collectors.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.*;

public class TicketOrderFactory {

    public TicketOrder toTicketOrder(List<TicketTypeRequest> ticketTypeRequests) {
        long ticketCount = getTicketCountFor(ticketTypeRequests);
        return new TicketOrder(ticketCount, 0, 0);
    }

    private long getTicketCountFor(List<TicketTypeRequest> ticketTypeRequests) {
        List<TicketTypeRequest> filteredRequests = ticketTypeRequests.stream()
                .filter(TicketOrderFactory::isAdultTicketType).collect(toList());
        return filteredRequests.stream()
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .summaryStatistics().getSum();
    }

    private static boolean isAdultTicketType(TicketTypeRequest request) {
        return Type.ADULT.equals(request.getTicketType());
    }
}
