package it.arrive.invoicesystem.invoice.services;

import it.arrive.invoicesystem.invoice.dto.InvoiceRequest;
import it.arrive.invoicesystem.invoice.dto.InvoiceResponse;
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse;
import it.arrive.invoicesystem.invoice.dto.PaymentRequest;
import it.arrive.invoicesystem.invoice.model.Invoice;

import java.util.UUID;

public interface InvoiceService {

    InvoicesResponse getInvoices( int page );

    Invoice createInvoice( InvoiceRequest invoiceRequest );

    void addItemToInvoice( UUID invoiceId, String itemSkuCode );

    void updatePaymentInfoForInvoice( UUID invoiceId, PaymentRequest paymentRequest );

    InvoiceResponse getInvoiceResponse( UUID invoiceId );
}
