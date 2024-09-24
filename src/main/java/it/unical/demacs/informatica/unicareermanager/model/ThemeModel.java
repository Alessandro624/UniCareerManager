package it.unical.demacs.informatica.unicareermanager.model;

public enum ThemeModel {
    LIGHT("light", "chiaro"),
    DARK("dark", "scuro");

    private final String name;
    private final String itName;

    ThemeModel(String name, String itName) {
        this.name = name;
        this.itName = itName;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return itName;
    }

    public static ThemeModel fromName(String name) {
        for (ThemeModel theme : ThemeModel.values()) {
            if (theme.name.equalsIgnoreCase(name) || theme.itName.equalsIgnoreCase(name)) {
                return theme;
            }
        }
        // Return a default value if no match is found
        return ThemeModel.LIGHT;
    }
}
