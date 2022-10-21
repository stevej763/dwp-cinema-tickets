package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketOrderFactory;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.CHILD;

public class TicketServiceImplTest {

    public static final TicketTypeRequest SINGLE_ADULT_TICKET_REQUEST = new TicketTypeRequest(ADULT, 1);
    private static final long VALID_ACCOUNT_ID = 1L;

    private final SeatReservationService seatReservationService = mock(SeatReservationService.class);
    private final TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
    private final AccountValidator accountValidator = mock(AccountValidator.class);
    private final TicketOrderFactory ticketOrderFactory = mock(TicketOrderFactory.class);

    private final TicketServiceImpl underTest = new TicketServiceImpl(seatReservationService, ticketPaymentService, accountValidator, ticketOrderFactory);


    @Before
    public void setUp() {
        when(accountValidator.isValidAccount(any())).thenReturn(true);
    }

    @Test
    public void processesRequestSuccessfullyWithValidParameters() {
        when(ticketOrderFactory.toTicketOrder(List.of(SINGLE_ADULT_TICKET_REQUEST))).thenReturn(new TicketOrder(1, 0, 0));
        underTest.purchaseTickets(VALID_ACCOUNT_ID, SINGLE_ADULT_TICKET_REQUEST);

        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, 0);
        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 0);
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderFromOneTicketTypeRequest() {
        TicketTypeRequest tickerRequestExceedingMaximum = new TicketTypeRequest(ADULT, 21);
        when(ticketOrderFactory.toTicketOrder(List.of(tickerRequestExceedingMaximum))).thenReturn(new TicketOrder(21, 0, 0));
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, tickerRequestExceedingMaximum);
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderAcrossMultipleTicketTypeRequests() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{
                new TicketTypeRequest(ADULT, 5),
                new TicketTypeRequest(ADULT, 5),
                new TicketTypeRequest(ADULT, 5),
                new TicketTypeRequest(ADULT, 5),
                new TicketTypeRequest(ADULT, 5)};
        when(ticketOrderFactory.toTicketOrder(List.of(ticketTypeRequests))).thenReturn(new TicketOrder(25, 0, 0));
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    @Test
    public void shouldThrowIfThereAreNoAdultTicketsInTheOrder() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(CHILD, 1);
        when(ticketOrderFactory.toTicketOrder(List.of(ticketTypeRequest))).thenReturn(new TicketOrder(0, 1, 0));
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequest);
            fail("Should throw InvalidPurchaseException when there are no adult tickets in the order");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: You must order at least one adult ticket"));
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
    public void shouldThrowIfThereAreNoTicketsInTheTicketTypeRequest() {
        TicketTypeRequest emptyTicketRequest = new TicketTypeRequest(ADULT, 0);
        when(ticketOrderFactory.toTicketOrder(List.of(emptyTicketRequest))).thenReturn(new TicketOrder(0, 0, 0));
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, emptyTicketRequest);
            fail("Should throw InvalidPurchaseException when there are no tickets in the any TicketTypeRequests");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: cannot process order with no tickets"));
        }
    }

    private void verifyOrderIsNotProcessedByPaymentOrReservationService() {
        verifyNoInteractions(ticketPaymentService);
        verifyNoInteractions(seatReservationService);
    }
}