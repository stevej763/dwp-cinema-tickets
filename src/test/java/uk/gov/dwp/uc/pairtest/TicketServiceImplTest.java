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
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

public class TicketServiceImplTest {

    private static final TicketTypeRequest SINGLE_ADULT_TICKET_REQUEST = new TicketTypeRequest(ADULT, 1);
    private static final long VALID_ACCOUNT_ID = 1L;

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
        when(orderValidator.checkForValidTicketOrder(List.of(SINGLE_ADULT_TICKET_REQUEST))).thenReturn(new TicketOrder(new TicketCount(1), null, null));
        underTest.purchaseTickets(VALID_ACCOUNT_ID, SINGLE_ADULT_TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 1);
    }

    @Test
    public void shouldThrowWhenOrderIsInvalid() {
        InvalidPurchaseException expectedException = new InvalidPurchaseException("exception message");
        final TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{SINGLE_ADULT_TICKET_REQUEST};
        doThrow(expectedException).when(orderValidator).checkForValidTicketOrder(List.of(ticketTypeRequests));

        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
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
}