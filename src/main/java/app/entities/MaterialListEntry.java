package app.entities;

public record MaterialListEntry(Material material, int amount, int orderId, String description) {

    MaterialListEntry(Material material, int amount){
        this(material, amount, 0, "");
    }
}
