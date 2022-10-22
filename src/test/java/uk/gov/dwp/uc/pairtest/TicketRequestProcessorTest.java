package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.*;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.uc.pairtest.helpers.TicketOrderTestHelper.aTicketOrder;

public class TicketRequestProcessorTest {

    private static final TicketTypeRequest TICKET_TYPE_REQUEST = mock(TicketTypeRequest.class);
    private static final List<TicketTypeRequest> TICKET_REQUEST_LIST = List.of(TICKET_TYPE_REQUEST);

    private final TicketOrderFactory ticketOrderFactory = mock(TicketOrderFactory.class);

    private final TicketRequestProcessor underTest = new TicketRequestProcessor(ticketOrderFactory);

    @Test
    public void returnsTicketOrderForSingleAdult() {
        TicketOrder expectedTicketOrder = aTicketOrder(1, 0, 0);
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.createValidTicketOrder(TICKET_REQUEST_LIST);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void returnsTicketOrderOfMultipleTicketsWithinLimit() {
        TicketOrder expectedTicketOrder = aTicketOrder(10, 5, 5);
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.createValidTicketOrder(TICKET_REQUEST_LIST);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void returnsTicketOrderWhenThereAreMoreAdultsThanInfants() {
        TicketOrder expectedTicketOrder = aTicketOrder(10, 0, 9);
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.createValidTicketOrder(TICKET_REQUEST_LIST);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void returnsTicketOrderWhenThereAreAnEqualNumberOfAdultsAndInfants() {
        TicketOrder expectedTicketOrder = aTicketOrder(10, 0, 10);
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.createValidTicketOrder(TICKET_REQUEST_LIST);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderFromOneTicketTypeRequest() {
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(aTicketOrder(21, 0, 0));
        try {
            underTest.createValidTicketOrder(TICKET_REQUEST_LIST);
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderAcrossMultipleTicketTypeRequests() {
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(aTicketOrder(10, 10, 5));
        try {
            underTest.createValidTicketOrder(TICKET_REQUEST_LIST);
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    @Test
    public void shouldThrowIfThereAreNoAdultTicketsInTheOrder() {
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(aTicketOrder(0, 1, 0));
        try {
            underTest.createValidTicketOrder(TICKET_REQUEST_LIST);
            fail("Should throw InvalidPurchaseException when there are no adult tickets in the order");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: You must order at least one adult ticket"));
        }
    }

    @Test
    public void shouldThrowIfThereAreNoTicketsInTheTicketTypeRequest() {
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(aTicketOrder(0, 0, 0));
        try {
            underTest.createValidTicketOrder(TICKET_REQUEST_LIST);
            fail("Should throw InvalidPurchaseException when there are no tickets in the any TicketTypeRequests");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: cannot process order with no tickets"));
        }
    }

    @Test
    public void shouldThrowIfThereMoreInfantsThanAdults() {
        when(ticketOrderFactory.toTicketOrder(TICKET_REQUEST_LIST)).thenReturn(aTicketOrder(1, 0, 2));
        try {
            underTest.createValidTicketOrder(TICKET_REQUEST_LIST);
            fail("Should throw InvalidPurchaseException when there are more infants than adults on the order");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: there must be at least one adult for every infant"));
        }
    }
}
