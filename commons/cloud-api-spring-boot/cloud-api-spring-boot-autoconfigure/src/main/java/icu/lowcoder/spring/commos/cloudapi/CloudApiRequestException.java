package icu.lowcoder.spring.commos.cloudapi;

public class CloudApiRequestException extends RuntimeException {
    public CloudApiRequestException() {
        super();
    }

    public CloudApiRequestException(String message) {
        super(message);
    }

    public CloudApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudApiRequestException(Throwable cause) {
        super(cause);
    }

    protected CloudApiRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
