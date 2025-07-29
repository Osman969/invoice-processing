package it.arrive.invoicesystem.invoice.exceptions;

public class InvoiceAlreadyPaidException extends RuntimeException {

    public InvoiceAlreadyPaidException( String message ) {
        super( message );
    }
}