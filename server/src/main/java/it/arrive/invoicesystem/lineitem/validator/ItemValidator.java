package it.arrive.invoicesystem.lineitem.validator;

import it.arrive.invoicesystem.common.util.Utils;
import org.springframework.stereotype.Component;

@Component
public class ItemValidator {

    public void validatePageNumber( int pageNumber ) {
        Utils.validatePageNumber( pageNumber );
    }
}