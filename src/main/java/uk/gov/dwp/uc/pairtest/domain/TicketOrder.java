package uk.gov.dwp.uc.pairtest.domain;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class TicketOrder {
    private final TicketCount adultTicketCount;
    private final TicketCount childTicketCount;
    private final TicketCount infantTicketCount;

    public TicketOrder(TicketCount adultTicketCount, TicketCount childTicketCount, TicketCount infantTicketCount) {
        this.adultTicketCount = adultTicketCount;
        this.childTicketCount = childTicketCount;
        this.infantTicketCount = infantTicketCount;
    }

    public TicketCount getAdultTicketCount() {
        return adultTicketCount;
    }

    public TicketCount getChildTicketCount() {
        return childTicketCount;
    }

    public TicketCount getInfantTicketCount() {
        return infantTicketCount;
    }

    public Long getAdultTicketCountAsLong() {
        return getAdultTicketCount().getNumberOfTickets();
    }

    public Long getChildTicketCountAsLong() {
        return getChildTicketCount().getNumberOfTickets();
    }

    public Long getInfantTicketCountAsLong() {
        return getInfantTicketCount().getNumberOfTickets();
    }

    public Long getTotalTicketCount() {
        return getAdultTicketCountAsLong() + getChildTicketCountAsLong() + getInfantTicketCountAsLong();
    }

    public int getTotalSeatCountForReservation() {
        return (int) (getAdultTicketCountAsLong() + getChildTicketCountAsLong());
    }

    @Override
    public boolean equals(Object obj) {
        return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
