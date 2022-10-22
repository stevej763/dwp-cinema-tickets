package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.OrderTotalPrice;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.TicketOrderTestHelper.aTicketOrder;

public class TicketServiceImplTest {

    private static final long VALID_ACCOUNT_ID = 1L;
    private static final int AMOUNT_TO_PAY = 1;
    private final static TicketTypeRequest TICKET_REQUEST = mock(TicketTypeRequest.class);
    private static final OrderTotalPrice ORDER_TOTAL = mock(OrderTotalPrice.class);

    private final SeatReservationService seatReservationService = mock(SeatReservationService.class);
    private final TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
    private final AccountValidator accountValidator = mock(AccountValidator.class);
    private final TicketRequestProcessor ticketRequestProcessor = mock(TicketRequestProcessor.class);
    private final PaymentCalculator paymentCalculator = mock(PaymentCalculator.class);

    private final TicketServiceImpl underTest = new TicketServiceImpl(
            accountValidator,
            ticketRequestProcessor,
            paymentCalculator,
            seatReservationService,
            ticketPaymentService);

    @Before
    public void setUp() {
        when(accountValidator.isValidAccount(any())).thenReturn(true);
        when(ORDER_TOTAL.getPaymentAmount()).thenReturn(AMOUNT_TO_PAY);
    }

    @Test
    public void chargesPaymentServiceForBookedSeats() {
        int expectedPaymentAmount = 250;
        OrderTotalPrice expectedTotalPrice = new OrderTotalPrice(expectedPaymentAmount);
        TicketOrder ticketOrder = aTicketOrder(10, 5, 0);

        when(ticketRequestProcessor.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(ticketOrder);
        when(paymentCalculator.calculateOrderTotalPrice(ticketOrder)).thenReturn(expectedTotalPrice);

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 15);
        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, expectedPaymentAmount);
    }

    @Test
    public void reservesSeatSuccessfullyForASingleAdultTicket() {
        TicketOrder ticketOrder = aTicketOrder(1, 0, 0);
        when(ticketRequestProcessor.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(ticketOrder);
        when(paymentCalculator.calculateOrderTotalPrice(ticketOrder)).thenReturn(ORDER_TOTAL);
        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 1);
        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, AMOUNT_TO_PAY);
    }

    @Test
    public void reservesSeatsSuccessfullyForAMultipleAdultTickets() {
        TicketOrder ticketOrder = aTicketOrder(20, 0, 0);
        when(ticketRequestProcessor.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(ticketOrder);
        when(paymentCalculator.calculateOrderTotalPrice(ticketOrder)).thenReturn(ORDER_TOTAL);

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 20);
        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, AMOUNT_TO_PAY);
    }

    @Test
    public void reservesSeatsSuccessfullyForAnOrderWithAdultsAndChildren() {
        TicketOrder ticketOrder = aTicketOrder(5, 5, 0);
        when(ticketRequestProcessor.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(ticketOrder);
        when(paymentCalculator.calculateOrderTotalPrice(ticketOrder)).thenReturn(ORDER_TOTAL);

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 10);
        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, AMOUNT_TO_PAY);
    }

    @Test
    public void reservesSeatsSuccessfullyForAnOrderWithAdultsChildrenAndInfants() {
        TicketOrder ticketOrder = aTicketOrder(5, 5, 5);
        when(ticketRequestProcessor.createValidTicketOrder(List.of(TICKET_REQUEST))).thenReturn(ticketOrder);
        when(paymentCalculator.calculateOrderTotalPrice(ticketOrder)).thenReturn(ORDER_TOTAL);

        underTest.purchaseTickets(VALID_ACCOUNT_ID, TICKET_REQUEST);

        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 10);
        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, AMOUNT_TO_PAY);
    }

    @Test
    public void shouldThrowWhenOrderIsInvalid() {
        InvalidPurchaseException expectedException = new InvalidPurchaseException("exception message");
        doThrow(expectedException).when(ticketRequestProcessor).createValidTicketOrder(List.of(TICKET_REQUEST));
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

    @Test
    public void shouldThrowWhenNoTicketTypeRequestProvided() {
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID);
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
