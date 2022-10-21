package uk.gov.dwp.uc.pairtest.domain;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketOrderFactoryTest {

    private static final TicketCount ZERO = new TicketCount(0);
    private static final TicketCount ONE = new TicketCount(1);
    private static final TicketCount TWENTY = new TicketCount(20);

    private final TicketOrderFactory underTest = new TicketOrderFactory();

    @Test
    public void canConvertTicketTypeRequestIntoATicketOrderWithNoTickets() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(ADULT, 0);

        TicketOrder result = underTest.toTicketOrder(List.of(ticketTypeRequest));

        assertThat(result, is(new TicketOrder(ZERO, ZERO, ZERO)));
    }

    @Test
    public void canConvertAdultTicketRequestIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(ADULT, 1);

        TicketOrder result = underTest.toTicketOrder(List.of(ticketTypeRequest));

        assertThat(result, is(new TicketOrder(ONE, ZERO, ZERO)));
    }

    @Test
    public void canConvertMultipleAdultTicketRequestsIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(ADULT, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(ADULT, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(ADULT, 5);
        TicketTypeRequest ticketTypeRequest4 = new TicketTypeRequest(ADULT, 5);

        TicketOrder result = underTest.toTicketOrder(List.of(
                ticketTypeRequest1,
                ticketTypeRequest2,
                ticketTypeRequest3,
                ticketTypeRequest4));

        assertThat(result, is(new TicketOrder(TWENTY, ZERO, ZERO)));
    }

    @Test
    public void canConvertChildTicketRequestIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(CHILD, 1);

        TicketOrder result = underTest.toTicketOrder(List.of(ticketTypeRequest));

        assertThat(result, is(new TicketOrder(ZERO, ONE, ZERO)));
    }

    @Test
    public void canConvertMultipleChildTicketRequestsIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(CHILD, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(CHILD, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(CHILD, 5);
        TicketTypeRequest ticketTypeRequest4 = new TicketTypeRequest(CHILD, 5);

        TicketOrder result = underTest.toTicketOrder(List.of(
                ticketTypeRequest1,
                ticketTypeRequest2,
                ticketTypeRequest3,
                ticketTypeRequest4));

        assertThat(result, is(new TicketOrder(ZERO, TWENTY, ZERO)));
    }

    @Test
    public void canConvertInfantTicketRequestIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(INFANT, 1);

        TicketOrder result = underTest.toTicketOrder(List.of(ticketTypeRequest));

        assertThat(result, is(new TicketOrder(ZERO, ZERO, ONE)));
    }

    @Test
    public void canConvertMultipleInfantTicketRequestsIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(INFANT, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(INFANT, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(INFANT, 5);
        TicketTypeRequest ticketTypeRequest4 = new TicketTypeRequest(INFANT, 5);

        TicketOrder result = underTest.toTicketOrder(List.of(
                ticketTypeRequest1,
                ticketTypeRequest2,
                ticketTypeRequest3,
                ticketTypeRequest4));

        assertThat(result, is(new TicketOrder(ZERO, ZERO, TWENTY)));
    }

    @Test
    public void canConvertMultipleMixedTicketRequestsIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(ADULT, 2);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(CHILD, 2);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(INFANT, 2);
        TicketTypeRequest ticketTypeRequest4 = new TicketTypeRequest(ADULT, 2);
        TicketTypeRequest ticketTypeRequest5 = new TicketTypeRequest(CHILD, 2);
        TicketTypeRequest ticketTypeRequest6 = new TicketTypeRequest(INFANT, 2);

        TicketOrder result = underTest.toTicketOrder(List.of(
                ticketTypeRequest1,
                ticketTypeRequest2,
                ticketTypeRequest3,
                ticketTypeRequest4,
                ticketTypeRequest5,
                ticketTypeRequest6));

        TicketCount expectedTicketCount = new TicketCount(4);
        TicketOrder expectedOrder = new TicketOrder(expectedTicketCount, expectedTicketCount, expectedTicketCount);
        assertThat(result, is(expectedOrder));
    }
}