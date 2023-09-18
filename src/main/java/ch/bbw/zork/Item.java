package ch.bbw.zork;

public class Item {

    private String itemName;
    private String description;
    private double itemWeight;

    public Item(String name, String desc, double weight) {
        itemName = name;
        description = desc;
        itemWeight = weight;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getItemWeight() {
        return itemWeight;
    }
}
