package cz.pb138.accounting.db;

/**
 * Error codes for AccountingException
 *
 * @author Tomas Madeja
 */
public class ADBErrorCodes {

    /**
     * Unexpected or unspecified error
     */
    public static int UNKNOWN_ERROR = 0;

    /**
     * Error code for wrong xml format
     */
    public static int XML_PARSING_ERROR = 1;

    /**
     * Error code for inability to read .xsd document for parsing
     */
    public static int XSD_READING_ERROR = 2;

    /**
     * Error code for conection problems
     */
    public static int CONNECTION_ERROR = 4;

    /**
     * Error code for insufficient privelages
     */
    public static int ACCESS_ERROR = 5;

    /**
     * Error code for problems starting database instance (server)
     */
    public static int DATABASE_INITIALIZATION_FAILURE = 9;

    /**
     * Error code for problems creatin AccountingDatabase object
     */
    public static int ACCDB_INSTANTIATING_FAILURE = 10;

    /**
     * Error code for problems terminating database instance (server)
     */
    public static int DATABASE_TERMINATION_FAILURE = 11;

    /**
     * Error code for problems commiting changes in resource
     */
    public static int RESOURCE_COMMIT_FAILURE = 12;

    /**
     * Error code for invalid date format inputed to database
     */
    public static int INVALID_DATE_FORMAT = 13;

    /**
     * Wrong input error
     */
    public static int WRONG_INPUT = 14;
}
