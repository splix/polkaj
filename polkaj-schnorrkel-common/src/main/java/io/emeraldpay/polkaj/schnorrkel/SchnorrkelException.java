package io.emeraldpay.polkaj.schnorrkel;

public class SchnorrkelException extends Exception {

    public SchnorrkelException() {
        super();
    }

    public SchnorrkelException(String message) {
        super(message);
    }

    public SchnorrkelException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchnorrkelException(Throwable cause) {
        super(cause);
    }

    protected SchnorrkelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
