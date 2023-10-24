package guru.springframework.spring6restmvc.controller;

public class BeerNotFoundException extends RuntimeException{
    public BeerNotFoundException() {
        super();
    }

    public BeerNotFoundException(String message) {
        super(message);
    }

    public BeerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeerNotFoundException(Throwable cause) {
        super(cause);
    }

    protected BeerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
