package uk.gov.dwp.uc.pairtest.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.dwp.uc.pairtest.TicketOrderTestHelper.aTicketOrder;

public class OrderReceiptTest {

    private final OrderTotalPrice orderTotalPrice = new OrderTotalPrice(10);
    private final OrderReceipt underTest = new OrderReceipt(aTicketOrder(1, 1, 1), orderTotalPrice);

    @Test
    public void returnsTotalNumberOfSeatsExcludingTheInfantSeatCount() {
        assertThat(underTest.getSeatCountToReserve(), is(2));
    }

    @Test
    public void returnsTotalPaymentAmountFromTheOrderTotal() {
        assertThat(underTest.getAmountToPay(), is(10));
    }
}
