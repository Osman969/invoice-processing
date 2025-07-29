package it.arrive.invoicesystem.invoice.services;

import it.arrive.invoicesystem.invoice.dto.InvoiceRequest;
import it.arrive.invoicesystem.invoice.dto.InvoiceResponse;
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse;
import it.arrive.invoicesystem.invoice.dto.PaymentRequest;
import it.arrive.invoicesystem.invoice.model.Invoice;

import java.util.UUID;

/**
 * Interface for handling invoice-related operations.
 * Supports creation, retrieval, item management, and payment updates.
 */
public interface InvoiceService {

    /**
     * Fetches a paginated list of invoices.
     * <ul>
     *     <li>Pagination size is driven by configuration.</li>
     * </ul>
     *
     * @param page the page number to fetch
     * @return {@link InvoicesResponse} containing invoices
     */
    InvoicesResponse getInvoices( int page );

    /**
     * Creates a new invoice from the provided request.
     * <ul>
     *     <li>Maps the request DTO to an invoice entity.</li>
     *     <li>Persists the invoice in the database.</li>
     * </ul>
     *
     * @param invoiceRequest the {@link InvoiceRequest} data
     * @return the saved {@link Invoice} entity
     */
    Invoice createInvoice( InvoiceRequest invoiceRequest );

    /**
     * Adds an item to an existing invoice.
     * <ul>
     *     <li>Validates the SKU and invoice existence.</li>
     *     <li>Fails if the item is out of stock.</li>
     * </ul>
     *
     * @param invoiceId   the UUID of the target invoice
     * @param itemSkuCode the SKU code of the item to add
     */
    void addItemToInvoice( UUID invoiceId, String itemSkuCode );

    /**
     * Updates the payment details of an invoice.
     * <ul>
     *     <li>Typically marks the invoice as paid and stores payment method or ref.</li>
     * </ul>
     *
     * @param invoiceId      the UUID of the invoice to update
     * @param paymentRequest the {@link PaymentRequest} data
     */
    void updatePaymentInfoForInvoice( UUID invoiceId, PaymentRequest paymentRequest );

    /**
     * Fetches detailed response object for a specific invoice.
     * <ul>
     *     <li>Includes metadata and line items if configured.</li>
     * </ul>
     *
     * @param invoiceId the UUID of the invoice
     * @return {@link InvoiceResponse} object with detailed invoice info
     */
    InvoiceResponse getInvoiceResponse( UUID invoiceId );
}
