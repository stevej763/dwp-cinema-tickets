package uk.gov.dwp.uc.pairtest.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
        return adultTicketCount.getCount();
    }

    public Long getChildTicketCountAsLong() {
        return childTicketCount.getCount();
    }

    public Long getInfantTicketCountAsLong() {
        return infantTicketCount.getCount();
    }

    public Long getTotalTicketCount() {
        return getAdultTicketCountAsLong() + getChildTicketCountAsLong() + getInfantTicketCountAsLong();
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }

    public int getTotalSeatCountForReservation() {
        return (int) (getAdultTicketCountAsLong() + getChildTicketCountAsLong());
    }
}
