package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.OrderPayment;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;

public class PaymentCalculator {

    public OrderPayment calculatePayment(TicketOrder tickerOrder) {
        int adultTicketPrice = (int) tickerOrder.getAdultTicketCount().getCount() * 20;
        return new OrderPayment(adultTicketPrice);
    }
}
