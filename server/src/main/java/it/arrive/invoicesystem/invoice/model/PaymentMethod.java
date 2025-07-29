package it.arrive.invoicesystem.invoice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    CASH( "Cash" ),
    CREDIT_CARD( "Credit Card" ),
    DEBIT_CARD( "Debit Card" ),
    BANK_TRANSFER( "Bank Transfer" ),
    PAYPAL( "PayPal" ),
    CHECK( "Check" ),
    OTHER( "Other" );

    private final String displayName;
}