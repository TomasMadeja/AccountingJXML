package cz.pb138.accounting.fn;

public enum ItemsType {
    NAME(8), UNIT(9), PRICE(10), QUANTITY(11);

    private Integer duck;
    ItemsType(int i) {
        duck = i;
    }

    Integer getValue() {
        return duck;
    }
}
