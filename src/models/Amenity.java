package models;

public class Amenity {
    private int id;
    private String name;
    private String iconPath;

    public Amenity(int id, String name, String iconPath) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }
}