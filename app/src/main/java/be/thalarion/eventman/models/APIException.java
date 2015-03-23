package be.thalarion.eventman.models;

/**
 * APIException - API error response
 */
public class APIException extends Exception {

    public APIException() {
    }

    public APIException(String detailMessage) {
        super(detailMessage);
    }

    public APIException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public APIException(Throwable throwable) {
        super(throwable);
    }
}
