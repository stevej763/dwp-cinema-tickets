package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.*;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class OrderValidatorTest {

    private final TicketOrderFactory ticketOrderFactory = mock(TicketOrderFactory.class);

    private final OrderValidator underTest = new OrderValidator(ticketOrderFactory);

    @Test
    public void returnsTicketOrderForSingleAdult() {
        TicketTypeRequest singleAdultTicket = new TicketTypeRequest(ADULT, 1);
        TicketOrder expectedTicketOrder = aTicketOrder(1, 0, 0);
        when(ticketOrderFactory.toTicketOrder(List.of(singleAdultTicket))).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.checkForValidTicketOrder(List.of(singleAdultTicket));

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void returnsTicketOrderOfMultipleTicketsWithinLimit() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(ADULT, 10);
        TicketTypeRequest childTicket = new TicketTypeRequest(CHILD, 5);
        TicketTypeRequest infantTicket = new TicketTypeRequest(INFANT, 5);
        List<TicketTypeRequest> ticketTypeRequests = List.of(adultTicket, childTicket, infantTicket);
        TicketOrder expectedTicketOrder = aTicketOrder(10, 5, 5);
        when(ticketOrderFactory.toTicketOrder(ticketTypeRequests)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.checkForValidTicketOrder(ticketTypeRequests);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void returnsTicketOrderWhenThereAreMoreAdultsThanInfants() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(ADULT, 10);
        TicketTypeRequest infantTicket = new TicketTypeRequest(INFANT, 9);
        List<TicketTypeRequest> ticketTypeRequests = List.of(adultTicket, infantTicket);
        TicketOrder expectedTicketOrder = aTicketOrder(10, 0, 9);
        when(ticketOrderFactory.toTicketOrder(ticketTypeRequests)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.checkForValidTicketOrder(ticketTypeRequests);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void returnsTicketOrderWhenThereAreAnEqualNumberOfAdultsAndInfants() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(ADULT, 10);
        TicketTypeRequest infantTicket = new TicketTypeRequest(INFANT, 10);
        List<TicketTypeRequest> ticketTypeRequests = List.of(adultTicket, infantTicket);
        TicketOrder expectedTicketOrder = aTicketOrder(10, 0, 10);
        when(ticketOrderFactory.toTicketOrder(ticketTypeRequests)).thenReturn(expectedTicketOrder);

        TicketOrder ticketOrder = underTest.checkForValidTicketOrder(ticketTypeRequests);

        assertThat(ticketOrder, is(expectedTicketOrder));
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderFromOneTicketTypeRequest() {
        TicketTypeRequest tickerRequestExceedingMaximum = new TicketTypeRequest(ADULT, 21);
        when(ticketOrderFactory.toTicketOrder(List.of(tickerRequestExceedingMaximum))).thenReturn(aTicketOrder(21, 0, 0));
        try {
            underTest.checkForValidTicketOrder(List.of(tickerRequestExceedingMaximum));
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    @Test
    public void shouldThrowIfThereAreMoreThan20TicketsInTheOrderAcrossMultipleTicketTypeRequests() {
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[]{
                new TicketTypeRequest(ADULT, 5),
                new TicketTypeRequest(CHILD, 5),
                new TicketTypeRequest(INFANT, 5),
                new TicketTypeRequest(CHILD, 5),
                new TicketTypeRequest(ADULT, 5)};
        when(ticketOrderFactory.toTicketOrder(List.of(ticketTypeRequests))).thenReturn(aTicketOrder(10, 10, 5));
        try {
            underTest.checkForValidTicketOrder(List.of(ticketTypeRequests));
            fail("Should throw InvalidPurchaseException when more than 20 tickets ordered at once");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    @Test
    public void shouldThrowIfThereAreNoAdultTicketsInTheOrder() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(CHILD, 1);
        when(ticketOrderFactory.toTicketOrder(List.of(ticketTypeRequest))).thenReturn(aTicketOrder(0, 1, 0));
        try {
            underTest.checkForValidTicketOrder(List.of(ticketTypeRequest));
            fail("Should throw InvalidPurchaseException when there are no adult tickets in the order");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: You must order at least one adult ticket"));
        }
    }

    @Test
    public void shouldThrowIfThereAreNoTicketsInTheTicketTypeRequest() {
        TicketTypeRequest emptyTicketRequest = new TicketTypeRequest(ADULT, 0);
        when(ticketOrderFactory.toTicketOrder(List.of(emptyTicketRequest))).thenReturn(aTicketOrder(0, 0, 0));
        try {
            underTest.checkForValidTicketOrder(List.of(emptyTicketRequest));
            fail("Should throw InvalidPurchaseException when there are no tickets in the any TicketTypeRequests");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: cannot process order with no tickets"));
        }
    }

    @Test
    public void shouldThrowIfThereMoreInfantsThanAdults() {
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(ADULT, 1);
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(ADULT, 2);
        List<TicketTypeRequest> ticketRequests = List.of(adultTicketRequest, infantTicketRequest);
        when(ticketOrderFactory.toTicketOrder(ticketRequests)).thenReturn(aTicketOrder(1, 0, 2));
        try {
            underTest.checkForValidTicketOrder(ticketRequests);
            fail("Should throw InvalidPurchaseException when there are more infants than adults on the order");
        } catch (InvalidPurchaseException exception) {
            assertThat(exception.getMessage(), is("Invalid order: there must be at least one adult for every infant"));
        }
    }


    private TicketOrder aTicketOrder(int count, int count1, int count11) {
        return new TicketOrder(new TicketCount(count), new TicketCount(count1), new TicketCount(count11));
    }
}