package cz.pb138.accounting.db;

import org.exist.security.Account;
import org.xmldb.api.base.XMLDBException;

/**
 * Interface for manipulating with accounting records
 *
 * @author Tomas Madeja
 */
public interface AccountingDatabase {

    /**
     * Starts database, if not already active. Will attempt to retrieve collection from database until successful.
     * @param path absolute path to given database
     * @return true if collection found, else false
     * @throws AccountingException contains error code associated to coresponding error
     */
    Boolean initDatabase(String path) throws AccountingException;

    /**
     * Starts database, if not already active. Will attempt to retrieve collection from database until successful or until waits count attempts.
     * @param path absolute path to given database
     * @param waits number of attempts, how many cycles should function wait for database to start
     * @return true if collection found, else false
     * @throws AccountingException contains error code associated to coresponding error
     */
    Boolean initDatabase(String path, long waits) throws AccountingException;

    /**
     * Kills database using default method. Only guarantees execution of the command, not its success
     * @param path absolute path to given database
     * @return True if command was successfully executed, else false
     * @throws AccountingException contains error code associated to coresponding error
     */
    Boolean killDatabase(String path) throws AccountingException;

    /**
     * Updates login information for database. Including null
     * @param username new username
     * @param password new password
     */
    void updateLogin(String username, String password);

    /**
     * Detects whether collection was found
     * @return true if collection found, else false
     */
    Boolean colFound();

    /**
     * Detecs whether Owner information is set within database
     * @return true if owner is set, false otherwise
     */
    Boolean isOwnerSet();

    /**
     * Stores single revenue record
     * @return true if successful, else false
     */
    Boolean addRevenue();

    /**
     * Stores single expenditure record
     * @return true if successful, else false
     */
    Boolean addExpenditure();

    /**
     * Find all records of revenue and expenditure in the given year
     */
    void getRecordsInYear();
}
