package cz.pb138.accounting.fn;

public enum InputType {
    NAME(1), ICO(2), DIC(3), ADDRESS(4), BANK(5);

    private Integer duck;
    InputType(int i) {
        duck = i;
    }

    Integer getValue() {
        return duck;
    }
}
