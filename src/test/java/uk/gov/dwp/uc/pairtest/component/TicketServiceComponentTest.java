package uk.gov.dwp.uc.pairtest.component;

import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.*;
import uk.gov.dwp.uc.pairtest.domain.TicketOrderFactory;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketServiceComponentTest {

    private final AccountValidator accountValidator = new AccountValidator();
    private final TicketOrderFactory ticketOrderFactory = new TicketOrderFactory();
    private final OrderValidator orderValidator = new OrderValidator(ticketOrderFactory);
    private final PaymentCalculator paymentCalculator = new PaymentCalculator();

    @Test
    public void canBookASetOfTickets() {
        SeatReservationService seatReservationService = mock(SeatReservationService.class);
        TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
        TicketService ticketService = new TicketServiceImpl(seatReservationService, ticketPaymentService, accountValidator, orderValidator, paymentCalculator);

        TicketTypeRequest[] ticketTypeRequests = anArrayOfTicketTypeRequests(5, 5, 5);

        ticketService.purchaseTickets(1L, ticketTypeRequests);

        verify(seatReservationService).reserveSeat(1L, 10);
        verify(ticketPaymentService).makePayment(1L, 150);
    }

    @Test
    public void doesNotBookTicketsIfThereIsAnInvalidRequest() {
        SeatReservationService seatReservationService = mock(SeatReservationService.class);
        TicketPaymentService ticketPaymentService = mock(TicketPaymentService.class);
        TicketService ticketService = new TicketServiceImpl(seatReservationService, ticketPaymentService, accountValidator, orderValidator, paymentCalculator);

        TicketTypeRequest[] ticketTypeRequests = anArrayOfTicketTypeRequests(500, 500, 500);

        try {
            ticketService.purchaseTickets(1L, ticketTypeRequests);
        } catch (InvalidPurchaseException invalidPurchaseException) {
            verifyNoInteractions(seatReservationService);
            verifyNoInteractions(ticketPaymentService);
            assertThat(invalidPurchaseException.getMessage(), is("Invalid order: You cannot purchase more than 20 tickets in one order"));
        }
    }

    private TicketTypeRequest[] anArrayOfTicketTypeRequests(int numberOfAdultTickets, int numberOfChildTickets, int numberOfInfantTickets) {
        return new TicketTypeRequest[]{
                new TicketTypeRequest(ADULT, numberOfAdultTickets),
                new TicketTypeRequest(CHILD, numberOfChildTickets),
                new TicketTypeRequest(INFANT, numberOfInfantTickets)
        };
    }
}
