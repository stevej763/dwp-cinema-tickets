package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.OrderTotal;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.TicketOrderTestHelper.aTicketOrder;

public class PaymentCalculatorTest {

    @Test
    public void canCalculateCostOfOneAdultTicket() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotal result = underTest.calculateOrderTotalPrice(aTicketOrder(1, 0, 0));

        OrderTotal expectedOrderTotal = new OrderTotal(20);
        assertThat(result, is(expectedOrderTotal));
    }

    @Test
    public void canCalculateCostOfMultipleAdultTickets() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotal result = underTest.calculateOrderTotalPrice(aTicketOrder(20, 0, 0));

        OrderTotal expectedOrderTotal = new OrderTotal(400);
        assertThat(result, is(expectedOrderTotal));
    }

    @Test
    public void canCalculateCostOfOneChildTicket() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotal result = underTest.calculateOrderTotalPrice(aTicketOrder(0, 1, 0));

        OrderTotal expectedOrderTotal = new OrderTotal(10);
        assertThat(result, is(expectedOrderTotal));
    }

    @Test
    public void canCalculateCostOfMultipleChildTickets() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotal result = underTest.calculateOrderTotalPrice(aTicketOrder(0, 20, 0));

        OrderTotal expectedOrderTotal = new OrderTotal(200);
        assertThat(result, is(expectedOrderTotal));
    }

    @Test
    public void infantTicketsDoNotIncreasePrice() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotal result = underTest.calculateOrderTotalPrice(aTicketOrder(0, 0, 20));

        OrderTotal expectedOrderTotal = new OrderTotal(0);
        assertThat(result, is(expectedOrderTotal));
    }

    @Test
    public void canCalculatePriceForAMixOfDifferentTicketTypes() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotal result = underTest.calculateOrderTotalPrice(aTicketOrder(10, 5, 5));

        OrderTotal expectedOrderTotal = new OrderTotal(250);
        assertThat(result, is(expectedOrderTotal));
    }
}