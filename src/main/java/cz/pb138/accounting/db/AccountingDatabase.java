package cz.pb138.accounting.db;

/**
 * Interface for manipulating with accounting records
 *
 * @author Tomas Madeja
 */
public interface AccountingDatabase {

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
