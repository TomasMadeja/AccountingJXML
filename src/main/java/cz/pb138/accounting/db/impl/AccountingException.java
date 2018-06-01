package cz.pb138.accounting.db.impl;

/**
 * Exceptions for AccountingDatabase
 *
 * @author Tomas Madeja
 */
public class AccountingException extends Exception {
    public final int errorCode;
    public final int passedErrorCode;

    public AccountingException(int errorCode) {
        super();
        this.errorCode = errorCode;
        this.passedErrorCode = 0;
    }

    public AccountingException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.passedErrorCode = 0;
    }

    public AccountingException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.passedErrorCode = 0;
    }

    public AccountingException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.passedErrorCode = 0;
    }

    public AccountingException(int errorCode, int passedErrorCode) {
        super();
        this.errorCode = errorCode;
        this.passedErrorCode = passedErrorCode;
    }

    public AccountingException(int errorCode, int passedErrorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.passedErrorCode = passedErrorCode;
    }

    public AccountingException(int errorCode, int passedErrorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.passedErrorCode = passedErrorCode;
    }

    public AccountingException(int errorCode, int passedErrorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.passedErrorCode = passedErrorCode;
    }
}
