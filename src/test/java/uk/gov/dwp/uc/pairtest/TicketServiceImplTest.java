package uk.gov.dwp.uc.pairtest;

import org.hamcrest.core.Is;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TicketServiceImplTest {

    private final TicketServiceImpl underTest = new TicketServiceImpl();

    @Test
    public void processesRequestWhenAccountIdIsValid() {
        SeatReservationService seatReservationService = mock(SeatReservationService.class);
        TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);

        underTest.purchaseTickets(1L, null);

        verify(ticketPaymentService).makePayment(1L, 0);
        verify(seatReservationService).reserveSeat(1L, 0);

    }
    @Test
    public void shouldThrowWhenAccountIdIsNull() {
        try {
            underTest.purchaseTickets(null, null);
            fail("Should throw InvalidPurchaseException when accountID is null");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=null"));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsZero() {
        try {
            underTest.purchaseTickets(0L, null);
            fail("Should throw InvalidPurchaseException when accountID is less than 1");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=0"));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsNegative() {
        try {
            underTest.purchaseTickets(-1L, null);
            fail("Should throw InvalidPurchaseException when accountID is less than 1");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=-1"));
        }
    }
}