package icu.lowcoder.spring.commos.cloudapi;

public class CloudApiException extends RuntimeException {
    public CloudApiException() {
        super();
    }

    public CloudApiException(String message) {
        super(message);
    }

    public CloudApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudApiException(Throwable cause) {
        super(cause);
    }

    protected CloudApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
