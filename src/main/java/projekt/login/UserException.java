package projekt.login;

/**
 * Výnimka pre chyby pri práci s používateľmi/databázou.
 */

public class UserException extends Exception {

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}