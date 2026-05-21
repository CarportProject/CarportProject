package app.entities;

public enum MaterialType {
    POST(1, "Stolper nedgraves 90 cm. i jord"),

    RAFTER(2, "Spær, monteres på rem"),
    REM(2, "Remme i sider, sadles ned i stolper"),

    WIDE_BOARD_FRONT(3, "Understernbrædder til for & bag ende"),
    WIDE_BOARD_SIDE(3, "Understernbrædder til siderne"),

    NARROW_BOARD_FRONT(4, "Oversternbrædder til forenden"),
    NARROW_BOARD_SIDE(4, "Oversternbrædder til siderne"),

    REGULAR(5, "Løsholter til skur gavle");

    private final int id;
    private final String description;

    MaterialType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
