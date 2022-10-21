package uk.gov.dwp.uc.pairtest.domain;

public class TicketOrder {
    private final long ticketCount;

    public TicketOrder(long ticketCount) {
        this.ticketCount = ticketCount;
    }

    public long getTicketCount() {
        return ticketCount;
    }
}
