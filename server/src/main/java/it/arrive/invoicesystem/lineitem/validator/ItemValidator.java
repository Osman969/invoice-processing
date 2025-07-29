package it.arrive.invoicesystem.lineitem.validator;

import it.arrive.invoicesystem.common.util.ValidationsUtils;
import it.arrive.invoicesystem.lineitem.dto.BasicLineItemWithDescAndPrice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ItemValidator {

    public void validatePageNumber( int pageNumber ) {
        ValidationsUtils.validatePageNumber( pageNumber );
    }

    public void validateLineItem( BasicLineItemWithDescAndPrice lineItem ) {
        ValidationsUtils.validateItemSkuCode( lineItem.getSku() );
        String description = lineItem.getDescription();
        if ( StringUtils.isBlank( description ) ) {
            throw new IllegalArgumentException(
                    "Item Desc '%s' is invalid. Item Desc cannot be blank".formatted( description )
            );
        }
        BigDecimal price = lineItem.getPrice();
        if ( price == null || price.compareTo( BigDecimal.ZERO ) <= 0 ) {
            throw new IllegalArgumentException(
                    "Item Price '%s' is invalid. Item Price must be greater than 0".formatted( price )
            );
        }
        Integer quantity = lineItem.getQuantity();
        if ( quantity == null || quantity <= 0 ) {
            throw new IllegalArgumentException(
                    "Item quantity '%s' is invalid. Item quantity must be greater than 0".formatted( quantity )
            );
        }
    }
}