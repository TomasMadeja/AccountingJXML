package cz.pb138.accounting.gui;

public class ItemTable {
    private String name;
    private String quantity;
    private String unit;
    private String price;
    private String desc;

    public ItemTable(String name,
                     String quantity,
                     String unit,
                     String price,
                     String desc) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.desc = desc;
    }

    public String getItemName() {
        return name;
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
