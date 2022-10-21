package uk.gov.dwp.uc.pairtest.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class TicketOrder {
    private final TicketCount adultTicketCount;
    private final TicketCount childTicketCount;
    private final TicketCount infantTicketCount;

    public TicketOrder(long adultTicketCount, long childTicketCount, long infantTicketCount) {
        this.adultTicketCount = new TicketCount(adultTicketCount);
        this.childTicketCount = new TicketCount(childTicketCount);
        this.infantTicketCount = new TicketCount(infantTicketCount);
    }

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

    public long getAdultTicketCountAsLong() {
        return adultTicketCount.getCount();
    }

    public long getChildTicketCountAsLong() {
        return childTicketCount.getCount();
    }

    public long getInfantTicketCountAsLong() {
        return infantTicketCount.getCount();
    }

    public long getTotalTicketCount() {
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
}
