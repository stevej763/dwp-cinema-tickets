package uk.gov.dwp.uc.pairtest;

import org.hamcrest.core.Is;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketServiceImplTest {

    private final SeatReservationService seatReservationService = mock(SeatReservationService.class);
    private final TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);

    private final TicketServiceImpl underTest = new TicketServiceImpl(seatReservationService, ticketPaymentService);

    @Test
    public void processesRequestWhenAccountIdIsValid() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(ADULT, 0);
        underTest.purchaseTickets(1L, ticketTypeRequest);

        verify(ticketPaymentService).makePayment(1L, 0);
        verify(seatReservationService).reserveSeat(1L, 0);

    }

    @Test
    public void shouldThrowWhenAccountIdIsNull() {
        try {
            underTest.purchaseTickets(null, null);
            fail("Should throw InvalidPurchaseException when accountID is null");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=null"));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsZero() {
        try {
            underTest.purchaseTickets(0L, null);
            fail("Should throw InvalidPurchaseException when accountID is less than 1");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=0"));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsNegative() {
        try {
            underTest.purchaseTickets(-1L, null);
            fail("Should throw InvalidPurchaseException when accountID is less than 1");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=-1"));
        }
    }

    @Test
    public void shouldThrowWhenTicketTypeRequestIsNull() {
        try {
            underTest.purchaseTickets(1L, null);
            fail("Should throw InvalidPurchaseException when TicketTypeRequest is null");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Cannot process order due to no TicketTypeRequests being received"));
        }
    }

    private void verifyOrderIsNotProcessedByPaymentOrReservationService() {
        verifyNoInteractions(ticketPaymentService);
        verifyNoInteractions(seatReservationService);
    }
}