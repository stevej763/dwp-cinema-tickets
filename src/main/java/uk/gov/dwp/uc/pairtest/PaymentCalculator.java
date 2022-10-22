package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.OrderTotalPrice;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;

public class PaymentCalculator {

    private static final int ADULT_TICKET_PRICE = 20;
    private static final int CHILD_TICKET_PRICE = 10;

    public OrderTotalPrice calculateOrderTotalPrice(TicketOrder tickerOrder) {
        int totalPrice = calculatePrice(tickerOrder);
        return new OrderTotalPrice(totalPrice);
    }

    private int calculatePrice(TicketOrder tickerOrder) {
        int adultTicketPrice = getTotalTicketPrice(tickerOrder.getAdultTicketCountAsLong(), ADULT_TICKET_PRICE);
        int childTicketPrice = getTotalTicketPrice(tickerOrder.getChildTicketCountAsLong(), CHILD_TICKET_PRICE);
        return adultTicketPrice + childTicketPrice;
    }

    private int getTotalTicketPrice(Long ticketCount, int ticketPrice) {
        return ticketCount.intValue() * ticketPrice;
    }
}
