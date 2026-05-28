package app.entities;

public record MaterialListEntry(Material material, int amount, int length, int orderId, String description) {

    MaterialListEntry(Material material, int amount, int length){
        this(material, amount, length, 0, "");
    }
}
