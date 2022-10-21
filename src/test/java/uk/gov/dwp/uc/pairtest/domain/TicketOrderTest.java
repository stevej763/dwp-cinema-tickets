package uk.gov.dwp.uc.pairtest.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class TicketOrderTest {

    @Test
    public void shouldReturnTotalTicketCount() {
        TicketOrder underTest = new TicketOrder(1, 1, 1);

        assertThat(underTest.getTotalTicketCount(), is(3L));
    }

}