package uk.gov.dwp.uc.pairtest;

import org.hamcrest.core.Is;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.*;

public class TicketServiceImplTest {

    @Test
    public void shouldThrowWhenAccountIdIsNull() {
        TicketServiceImpl underTest = new TicketServiceImpl();
        try {
            underTest.purchaseTickets(null, null);
            fail("Should throw InvalidPurchaseException when accountID is null");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), Is.is("Invalid account ID"));
        }
    }

}