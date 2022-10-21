package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

public class TicketServiceImplTest {

    private static final long VALID_ACCOUNT_ID = 1L;
    private final SeatReservationService seatReservationService = mock(SeatReservationService.class);
    private final TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
    private final AccountValidator accountValidator = mock(AccountValidator.class);

    private final TicketServiceImpl underTest = new TicketServiceImpl(seatReservationService, ticketPaymentService, accountValidator);

    @Before
    public void setUp() {
        when(accountValidator.isValidAccount(any())).thenReturn(true);
    }

    @Test
    public void processesRequestSuccessfullyWithValidParameters() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(ADULT, 1);
        underTest.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequest);

        verify(ticketPaymentService).makePayment(VALID_ACCOUNT_ID, 0);
        verify(seatReservationService).reserveSeat(VALID_ACCOUNT_ID, 0);
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderFromOneTicketTypeRequest() {
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, new TicketTypeRequest(ADULT, 21));
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
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            verifyOrderIsNotProcessedByPaymentOrReservationService();
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
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
        try {
            underTest.purchaseTickets(VALID_ACCOUNT_ID, new TicketTypeRequest(ADULT, 0));
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