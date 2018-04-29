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
     * Starts database, if not already active. Uses default sleep (60 iterations of sleep(1000))
     * @param path absolute path to given database
     * @return true if collection found, else false
     * @throws XMLDBException
     */
    Boolean initDatabase(String path) throws XMLDBException;

    /**
     * Starts database, if not already active. Uses number of iterations based on waits parameter waits times sleep(1000)
     * @param path absolute path to given database
     * @param waits number of sleep cycles
     * @return true if collection found, else false
     * @throws XMLDBException
     */
    Boolean initDatabase(String path, long waits) throws XMLDBException;

    /**
     * Kills database using default method
     * @param path absolute path to given database
     * @return kill process
     */
    Process killDatabase(String path);

    /**
     * Detects whether collection was found
     * @return true if collection found, else false
     */
    Boolean colFound();

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
