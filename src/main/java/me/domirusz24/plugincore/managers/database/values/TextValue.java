package me.domirusz24.plugincore.managers.database.values;

public class TextValue extends DataBaseValue<String> {
    public TextValue(String name, String defaultValue) {
        super(name, defaultValue);
    }

    public TextValue(String name) {
        super(name, "NONE");
    }

    @Override
    public String getValue() {
        return "text";
    }
}
