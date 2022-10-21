package uk.gov.dwp.uc.pairtest.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class TicketOrder {
    private final long adultTicketCount;
    private final long childTicketCount;
    private final long infantTicketCount;

    public TicketOrder() {
        this(0, 0 ,0);
    }

    public TicketOrder(long adultTicketCount, long childTicketCount, long infantTicketCount) {
        this.adultTicketCount = adultTicketCount;
        this.childTicketCount = childTicketCount;
        this.infantTicketCount = infantTicketCount;
    }

    public long getAdultTicketCount() {
        return adultTicketCount;
    }

    public long getChildTicketCount() {
        return childTicketCount;
    }

    public long getInfantTicketCount() {
        return infantTicketCount;
    }

    public long getTotalTicketCount() {
        return getAdultTicketCount() + getChildTicketCount() + getInfantTicketCount();
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
