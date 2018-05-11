package cz.pb138.accounting.fn;

public enum ContactType {
    EMAIL(6), TELEPHONE(7);

    private Integer duck;
    ContactType(int i) {
        duck = i;
    }

    Integer getValue() {
        return duck;
    }
}
