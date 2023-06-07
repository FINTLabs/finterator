package no.fintlabs;

public class CustomerObjectResponseException extends RuntimeException {
    public CustomerObjectResponseException(String errorMessage) {
        super(errorMessage);
    }
}
