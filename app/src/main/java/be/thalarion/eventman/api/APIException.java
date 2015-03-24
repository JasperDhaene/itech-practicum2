package be.thalarion.eventman.api;

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
