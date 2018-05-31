package cz.pb138.accounting.fn;

public enum DateType {
    DATE(12);

    private Integer duck;
    DateType(int i) {
        duck = i;
    }

    Integer getValue() {
        return duck;
    }
}
