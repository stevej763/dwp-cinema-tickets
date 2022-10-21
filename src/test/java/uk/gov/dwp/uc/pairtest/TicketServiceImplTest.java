package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketCount;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    private static final long VALID_ACCOUNT_ID = 1L;
    private final static TicketTypeRequest TICKET_REQUEST = mock(TicketTypeRequest.class);

    private final SeatReservationService seatReservationService = mock(SeatReservationService.class);
    private final TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
    private final AccountValidator accountValidator = mock(AccountValidator.class);
    private final OrderValidator orderValidator = mock(OrderValidator.class);

    private final TicketServiceImpl underTest = new TicketServiceImpl(
            seatReservationService,
            ticketPaymentService,
            accountValidator,
            orderValidator);

    @Before
    public void setUp() {
        when(accountValidator.isValidAccount(any())).thenReturn(true);
    }

    @Test
    public void reservesSeatSuccessfullyForASingleAdultTicket() {
        when(orderValidator.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(aTickerOrder(1, 0, 0));
        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 1);
    }

    @Test
    public void reservesSeatsSuccessfullyForAMultipleAdultTickets() {
        when(orderValidator.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(aTickerOrder(20, 0, 0));

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 20);
    }

    @Test
    public void reservesSeatsSuccessfullyForAnOrderWithAdultsAndChildren() {
        when(orderValidator.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(aTickerOrder(5, 5, 0));

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 10);
    }

    @Test
    public void reservesSeatsSuccessfullyForAnOrderWithAdultsChildrenAndInfants() {
        when(orderValidator.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(aTickerOrder(5, 5, 5));

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 10);
    }

    @Test
    public void shouldThrowWhenOrderIsInvalid() {
        InvalidPurchaseException expectedException = new InvalidPurchaseException("exception message");
        doThrow(expectedException).when(orderValidator).createValidTicketOrder(List.of(TICKET_REQUEST));
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);
            fail("Should throw InvalidPurchaseException when order request is invalid");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception, is(expectedException));
        }
    }

    @Test
    public void shouldThrowWhenAccountIdIsInvalid() {
        when(accountValidator.isValidAccount(any())).thenReturn(false);
        try {
            underTest.purchaseTickets(null);
            fail("Should throw InvalidPurchaseException when accountID is null");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid account ID: accountId=null"));
        }
    }

    @Test
    public void shouldThrowWhenTicketTypeRequestIsNull() {
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, null);
            fail("Should throw InvalidPurchaseException when TicketTypeRequest is null");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: Cannot process order due to no TicketTypeRequests being received"));
        }
    }

    @Test
    public void shouldThrowWhenTicketTypeRequestIsEmptyArray() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[0];
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
            fail("Should throw InvalidPurchaseException when TicketTypeRequests is empty array");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: Cannot process order due to no TicketTypeRequests being received"));
        }
    }

    private void verifyOrderIsNotProcessedByPaymentOrReservationService() {
        verifyNoInteractions(ticketPaymentService);
        verifyNoInteractions(seatReservationService);
    }

    private TicketOrder aTickerOrder(int adultTicketCount, int childTicketCount, int infantTicketCount) {
        return new TicketOrder(new TicketCount(adultTicketCount), new TicketCount(childTicketCount), new TicketCount(infantTicketCount));
    }
}