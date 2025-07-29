package it.arrive.invoicesystem.common.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException( String message ) {
        super( message );
    }
}