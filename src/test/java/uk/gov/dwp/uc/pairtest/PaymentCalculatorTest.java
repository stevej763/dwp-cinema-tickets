package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.OrderPayment;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.TicketOrderTestHelper.aTicketOrder;

public class PaymentCalculatorTest {

    @Test
    public void canCalculateCostOfOneAdultTicket() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderPayment result = underTest.calculatePayment(aTicketOrder(1, 0, 0));

        OrderPayment expectedOrderPayment = new OrderPayment(20);
        assertThat(result, is(expectedOrderPayment));
    }

    @Test
    public void canCalculateCostOfMultipleAdultTickets() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderPayment result = underTest.calculatePayment(aTicketOrder(20, 0, 0));

        OrderPayment expectedOrderPayment = new OrderPayment(400);
        assertThat(result, is(expectedOrderPayment));
    }
}