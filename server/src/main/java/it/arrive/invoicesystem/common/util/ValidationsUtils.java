package it.arrive.invoicesystem.common.util;

import org.apache.commons.lang3.StringUtils;

public class ValidationsUtils {

    public static void validatePageNumber( int pageNumber ) {
        if ( pageNumber < 0 ) {
            throw new IllegalArgumentException(
                    String.format( "Page number '%s' is invalid. Page number must be greater than or equal to 0", pageNumber )
            );
        }
    }

    public static void validateItemSkuCode( String itemSkuCode ) {
        if ( StringUtils.isBlank( itemSkuCode ) ) {
            throw new IllegalArgumentException(
                    "Item SKU code '%s' is invalid. Item SKU code cannot be blank".formatted( itemSkuCode )
            );
        }
    }
}
