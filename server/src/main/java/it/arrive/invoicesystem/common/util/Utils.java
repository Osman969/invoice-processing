package it.arrive.invoicesystem.common.util;

public class Utils {

    public static void validatePageNumber( int pageNumber ) {
        if ( pageNumber < 0 ) {
            throw new IllegalArgumentException(
                    String.format( "Page number '%s' is invalid. Page number must be greater than or equal to 0", pageNumber )
            );
        }
    }
}
