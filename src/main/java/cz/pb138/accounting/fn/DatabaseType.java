package cz.pb138.accounting.fn;

public enum DatabaseType {
    OWNER(1), RECORDS(2);

    private Integer duck;
    DatabaseType(int i) {
        duck = i;
    }

    Integer getValue() {
        return duck;
    }
}
