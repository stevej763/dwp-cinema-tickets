package uk.gov.dwp.uc.pairtest.domain;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TicketOrderTest {

    private final TicketCount ticketCount = new TicketCount(1);

    private final TicketOrder underTest = new TicketOrder(ticketCount, ticketCount, ticketCount);;

    @Test
    public void shouldReturnTotalTicketCount() {
        assertThat(underTest.getTotalTicketCount(), is(3L));
    }

    @Test
    public void returnsAdultTicketCountAsLong() {
        assertThat(underTest.getAdultTicketCountAsLong(), is(1L));
    }

    @Test
    public void returnsChildTicketCountAsLong() {
        assertThat(underTest.getAdultTicketCountAsLong(), is(1L));
    }

    @Test
    public void returnsInfantTicketCountAsLong() {
        assertThat(underTest.getAdultTicketCountAsLong(), is(1L));
    }
}
