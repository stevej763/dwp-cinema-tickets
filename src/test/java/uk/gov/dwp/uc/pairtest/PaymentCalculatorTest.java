package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.OrderTotalPrice;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.TicketOrderTestHelper.aTicketOrder;

public class PaymentCalculatorTest {

    @Test
    public void canCalculateCostOfOneAdultTicket() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotalPrice result = underTest.calculateOrderTotalPrice(aTicketOrder(1, 0, 0));

        OrderTotalPrice expectedOrderTotalPrice = new OrderTotalPrice(20);
        assertThat(result, is(expectedOrderTotalPrice));
    }

    @Test
    public void canCalculateCostOfMultipleAdultTickets() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotalPrice result = underTest.calculateOrderTotalPrice(aTicketOrder(20, 0, 0));

        OrderTotalPrice expectedOrderTotalPrice = new OrderTotalPrice(400);
        assertThat(result, is(expectedOrderTotalPrice));
    }

    @Test
    public void canCalculateCostOfOneChildTicket() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotalPrice result = underTest.calculateOrderTotalPrice(aTicketOrder(0, 1, 0));

        OrderTotalPrice expectedOrderTotalPrice = new OrderTotalPrice(10);
        assertThat(result, is(expectedOrderTotalPrice));
    }

    @Test
    public void canCalculateCostOfMultipleChildTickets() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotalPrice result = underTest.calculateOrderTotalPrice(aTicketOrder(0, 20, 0));

        OrderTotalPrice expectedOrderTotalPrice = new OrderTotalPrice(200);
        assertThat(result, is(expectedOrderTotalPrice));
    }

    @Test
    public void infantTicketsDoNotIncreasePrice() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotalPrice result = underTest.calculateOrderTotalPrice(aTicketOrder(0, 0, 20));

        OrderTotalPrice expectedOrderTotalPrice = new OrderTotalPrice(0);
        assertThat(result, is(expectedOrderTotalPrice));
    }

    @Test
    public void canCalculatePriceForAMixOfDifferentTicketTypes() {
        PaymentCalculator underTest = new PaymentCalculator();

        OrderTotalPrice result = underTest.calculateOrderTotalPrice(aTicketOrder(10, 5, 5));

        OrderTotalPrice expectedOrderTotalPrice = new OrderTotalPrice(250);
        assertThat(result, is(expectedOrderTotalPrice));
    }
}