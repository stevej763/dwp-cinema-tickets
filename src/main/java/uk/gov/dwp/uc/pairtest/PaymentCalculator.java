package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.OrderTotal;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;

public class PaymentCalculator {

    private static final int ADULT_TICKET_PRICE = 20;
    private static final int CHILD_TICKET_PRICE = 10;

    public OrderTotal calculateOrderTotalPrice(TicketOrder tickerOrder) {
        int totalPrice = calculatePrice(tickerOrder);
        return new OrderTotal(totalPrice);
    }

    private int calculatePrice(TicketOrder tickerOrder) {
        int adultTicketPrice = getTicketCostTotal(tickerOrder.getAdultTicketCountAsLong(), ADULT_TICKET_PRICE);
        int childTicketPrice = getTicketCostTotal(tickerOrder.getChildTicketCountAsLong(), CHILD_TICKET_PRICE);
        return adultTicketPrice + childTicketPrice;
    }

    private int getTicketCostTotal(Long tickerOrder, int adultTicketPrice) {
        return tickerOrder.intValue() * adultTicketPrice;
    }
}
