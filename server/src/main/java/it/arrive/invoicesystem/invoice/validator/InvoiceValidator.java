package it.arrive.invoicesystem.invoice.validator;

import it.arrive.invoicesystem.common.util.Utils;
import it.arrive.invoicesystem.invoice.model.PaymentMethod;
import it.arrive.invoicesystem.lineitem.model.LineItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class InvoiceValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9]([a-zA-Z0-9._+-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9.-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$"
    );

    public void validatePageNumber( int pageNumber ) {
        Utils.validatePageNumber( pageNumber );
    }

    public void validateItemSkuCode( String itemSkuCode ) {
        if ( StringUtils.isBlank( itemSkuCode ) ) {
            throw new IllegalArgumentException(
                    "Item SKU code '%s' is invalid. Item SKU code cannot be blank".formatted( itemSkuCode )
            );
        }
    }

    public void validatePaymentMethod( PaymentMethod paymentMethod ) {
        if ( paymentMethod == null ) {
            throw new IllegalArgumentException( "Payment method cannot be null" );
        }
    }

    public void validateInvoiceItems( List<LineItem> items ) {
        if ( CollectionUtils.isEmpty( items ) ) {
            throw new IllegalArgumentException( "Invoice must have at least one item" );
        }
    }

    public void validateCustomerEmail( String customerEmail ) {
        if ( StringUtils.isBlank( customerEmail ) || !EMAIL_PATTERN.matcher( customerEmail ).matches() ) {
            throw new IllegalArgumentException( "Customer email '%s' is invalid".formatted( customerEmail ) );
        }
    }
}