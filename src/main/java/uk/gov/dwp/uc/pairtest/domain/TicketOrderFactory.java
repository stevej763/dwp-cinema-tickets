package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Stream;

public class TicketOrderFactory {

    public TicketOrder toTicketOrder(List<TicketTypeRequest> ticketTypeRequests) {
        IntSummaryStatistics ticketCount = ticketTypeRequests.stream()
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .summaryStatistics();
        return new TicketOrder(ticketCount.getSum());
    }
}
