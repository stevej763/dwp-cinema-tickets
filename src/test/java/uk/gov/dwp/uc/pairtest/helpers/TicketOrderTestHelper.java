package uk.gov.dwp.uc.pairtest.helpers;

import uk.gov.dwp.uc.pairtest.domain.TicketCount;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;

public class TicketOrderTestHelper {

    public static TicketOrder aTicketOrder(int adultTicketCount, int childTicketCount, int infantTicketCount) {
        return new TicketOrder(new TicketCount(adultTicketCount), new TicketCount(childTicketCount), new TicketCount(infantTicketCount));
    }
}
