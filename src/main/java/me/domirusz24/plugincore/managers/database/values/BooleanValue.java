package me.domirusz24.plugincore.managers.database.values;


public class BooleanValue extends DataBaseValue<Boolean> {

    public BooleanValue(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    public BooleanValue(String name) {
        this(name, true);
    }

    @Override
    public String getValue() {
        return "bool";
    }
}
