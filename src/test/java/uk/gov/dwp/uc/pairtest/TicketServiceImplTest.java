package uk.gov.dwp.uc.pairtest;

import org.hamcrest.core.Is;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class TicketServiceImplTest {

    @Test
    public void shouldThrowWhenAccountIdIsNull() {
        TicketServiceImpl underTest = new TicketServiceImpl();
        try {
            underTest.purchaseTickets(null, null);
            fail("Should throw InvalidPurchaseException when accountID is null");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=null"));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsZero() {
        TicketServiceImpl underTest = new TicketServiceImpl();
        try {
            underTest.purchaseTickets(0L, null);
            fail("Should throw InvalidPurchaseException when accountID is less than 1");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=0"));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsNegative() {
        TicketServiceImpl underTest = new TicketServiceImpl();
        try {
            underTest.purchaseTickets(-1L, null);
            fail("Should throw InvalidPurchaseException when accountID is less than 1");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=-1"));
        }
    }
}