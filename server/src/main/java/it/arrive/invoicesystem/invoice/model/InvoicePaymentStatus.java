package it.arrive.invoicesystem.invoice.model;

import lombok.Getter;

@Getter
public enum InvoicePaymentStatus {

    PENDING( "Pending" ),
    PAID( "Paid" );

    private final String displayName;

    InvoicePaymentStatus( String displayName ) {
        this.displayName = displayName;
    }
}
