package me.domirusz24.plugincore.managers.database.values;

public class MediumTextValue extends DataBaseValue<String> {
    public MediumTextValue(String name, String defaultValue) {
        super(name, defaultValue);
    }

    public MediumTextValue(String name) {
        super(name, "NONE");
    }

    @Override
    public String getValue() {
        return "mediumtext";
    }
}
