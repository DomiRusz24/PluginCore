package me.domirusz24.plugincore.managers.database.values;

public class StringValue extends DataBaseValue<String> {

    private final int size;

    public StringValue(String name, int size, String defaultValue) {
        super(name, defaultValue);
        this.size = size;
    }

    public StringValue(String name, int size) {
        this(name, size, "NONE");
    }

    @Override
    public String getValue() {
        return "varchar(" + size + ")";
    }
}
