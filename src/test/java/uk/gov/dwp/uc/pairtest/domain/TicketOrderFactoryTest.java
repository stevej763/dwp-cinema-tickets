package uk.gov.dwp.uc.pairtest.domain;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

public class TicketOrderFactoryTest {

    private final TicketOrderFactory underTest = new TicketOrderFactory();

    @Test
    public void canConvertTicketTypeRequestIntoATicketOrderWithOneItemInList() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(ADULT, 1);

        TicketOrder result = underTest.toTicketOrder(List.of(ticketTypeRequest));

        assertThat(result, is(new TicketOrder(1, 0, 0)));
    }

    @Test
    public void canConvertTicketTypeRequestIntoATicketOrderWithNoTickets() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(ADULT, 0);

        TicketOrder result = underTest.toTicketOrder(List.of(ticketTypeRequest));

        assertThat(result, is(new TicketOrder(0, 0, 0)));
    }

    @Test
    public void canConvertMultipleTicketTypeRequestsIntoATicketOrder() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(ADULT, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(ADULT, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(ADULT, 5);
        TicketTypeRequest ticketTypeRequest4 = new TicketTypeRequest(ADULT, 5);

        TicketOrder result = underTest.toTicketOrder(List.of(
                ticketTypeRequest1,
                ticketTypeRequest2,
                ticketTypeRequest3,
                ticketTypeRequest4));

        assertThat(result, is(new TicketOrder(20, 0, 0)));
    }

}