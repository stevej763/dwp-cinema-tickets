package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.OrderTotal;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;

public class PaymentCalculator {

    private static final int ADULT_TICKET_PRICE = 20;
    private static final int CHILD_TICKET_PRICE = 10;

    public OrderTotal calculatePayment(TicketOrder tickerOrder) {
        int adultTicketPrice = tickerOrder.getAdultTicketCountAsLong().intValue() * ADULT_TICKET_PRICE;
        int childTicketPrice = tickerOrder.getChildTicketCountAsLong().intValue() * CHILD_TICKET_PRICE;
        int totalPrice = adultTicketPrice + childTicketPrice;
        return new OrderTotal(totalPrice);
    }
}
