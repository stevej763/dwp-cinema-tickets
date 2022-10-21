package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.OrderTotal;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;


public class TicketServiceImpl implements TicketService {

    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;
    private final AccountValidator accountValidator;
    private final OrderValidator orderValidator;
    private final PaymentCalculator paymentCalculator;

    public TicketServiceImpl(
            SeatReservationService seatReservationService,
            TicketPaymentService ticketPaymentService,
            AccountValidator accountValidator,
            OrderValidator orderValidator,
            PaymentCalculator paymentCalculator) {
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
        this.accountValidator = accountValidator;
        this.orderValidator = orderValidator;
        this.paymentCalculator = paymentCalculator;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountIdIsValid(accountId);
        checkForEmptyArrayOfTicketTypeRequests(ticketTypeRequests);
        TicketOrder ticketOrder = orderValidator.createValidTicketOrder(List.of(ticketTypeRequests));
        OrderTotal orderTotal = paymentCalculator.calculateOrderTotalPrice(ticketOrder);
        seatReservationService.reserveSeat(accountId, ticketOrder.getTotalSeatCountForReservation());
        ticketPaymentService.makePayment(accountId, orderTotal.getPaymentAmount());
    }

    private void checkAccountIdIsValid(Long accountId) {
        if (!accountValidator.isValidAccount(accountId)) {
            String message = String.format("Invalid account ID: accountId=%s", accountId);
            throw new InvalidPurchaseException(message);
        }
    }

    private void checkForEmptyArrayOfTicketTypeRequests(TicketTypeRequest[] ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            String message = "Invalid order: Cannot process order due to no TicketTypeRequests being received";
            throw new InvalidPurchaseException(message);
        }
    }
}
