package cz.pb138.accounting.db;

public interface Item {

    String value(String name);

    String name();

    String description();

    String quanitity();

    String unit();

    String price();

    Item changeValue(String name, String value);

    Item setName(String value);

    Item setDescription(String value);

    Item setQuantity(String value);

    Item setUnit(String value);

    Item setPrice(String value);

    void delete();
}
