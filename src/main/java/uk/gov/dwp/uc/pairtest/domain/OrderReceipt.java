package uk.gov.dwp.uc.pairtest.domain;

public class OrderReceipt {

    private final TicketOrder ticketOrder;
    private final OrderTotalPrice orderTotalPrice;

    public OrderReceipt(TicketOrder ticketOrder, OrderTotalPrice orderTotalPrice) {
        this.ticketOrder = ticketOrder;
        this.orderTotalPrice = orderTotalPrice;
    }

    public TicketOrder getTicketOrder() {
        return ticketOrder;
    }

    public OrderTotalPrice getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public int getSeatCountToReserve() {
        return (int) (getTicketOrder().getAdultTicketCountAsLong() + getTicketOrder().getChildTicketCountAsLong());
    }

    public int getAmountToPay() {
        return getOrderTotalPrice().getPaymentAmount();
    }
}
