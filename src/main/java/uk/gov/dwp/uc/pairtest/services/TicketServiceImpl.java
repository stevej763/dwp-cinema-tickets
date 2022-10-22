package uk.gov.dwp.uc.pairtest.services;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.OrderReceipt;
import uk.gov.dwp.uc.pairtest.domain.OrderTotalPrice;
import uk.gov.dwp.uc.pairtest.domain.TicketOrder;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.List;


public class TicketServiceImpl implements TicketService {

    private final AccountValidator accountValidator;
    private final TicketRequestProcessor ticketRequestProcessor;
    private final PaymentCalculator paymentCalculator;
    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;

    public TicketServiceImpl(
            AccountValidator accountValidator,
            TicketRequestProcessor ticketRequestProcessor,
            PaymentCalculator paymentCalculator,
            SeatReservationService seatReservationService,
            TicketPaymentService ticketPaymentService) {
        this.accountValidator = accountValidator;
        this.ticketRequestProcessor = ticketRequestProcessor;
        this.paymentCalculator = paymentCalculator;
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkParametersAreValid(accountId, ticketTypeRequests);
        OrderReceipt orderReceipt = createOrderReceipt(List.of(ticketTypeRequests));
        processPurchase(accountId, orderReceipt);
    }

    private void checkParametersAreValid(Long accountId, TicketTypeRequest[] ticketTypeRequests) {
        checkAccountIdIsValid(accountId);
        checkForEmptyArrayOfTicketTypeRequests(ticketTypeRequests);
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

    private OrderReceipt createOrderReceipt(List<TicketTypeRequest> ticketTypeRequestList) {
        TicketOrder ticketOrder = ticketRequestProcessor.createValidTicketOrder(ticketTypeRequestList);
        OrderTotalPrice orderTotalPrice = paymentCalculator.calculateOrderTotalPrice(ticketOrder);
        return new OrderReceipt(ticketOrder, orderTotalPrice);
    }

    private void processPurchase(Long accountId, OrderReceipt orderReceipt) {
        seatReservationService.reserveSeat(accountId, orderReceipt.getSeatCountToReserve());
        ticketPaymentService.makePayment(accountId, orderReceipt.getAmountToPay());
    }
}
