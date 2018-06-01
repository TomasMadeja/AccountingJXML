package cz.pb138.accounting.gui;

public class ItemTable {
    private String nameVal;
    private String quantity;
    private String unit;
    private String price;
    private String desc;

    public ItemTable(String nameVal,
                     String desc,
                     String quantity,
                     String unit,
                     String price) {
        this.nameVal = nameVal;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.desc = desc;
    }

    public String getNameVal() {
        return nameVal;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getPrice() {
        return price;
    }

    public String getDesc() {
        return desc;
    }
}
