package me.domirusz24.plugincore.core.stringobject;

import java.util.function.Supplier;

public class BooleanObj extends StringObject<Boolean> {

    static {
        new BooleanObj(null);
    }

    public BooleanObj(String name) {
        this(name, null);
    }

    public BooleanObj(String name, DefaultValue<Boolean> defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public StringObject<Boolean> defaultObject() {
        return this;
    }

    @Override
    public Class<Boolean> getClazz() {
        return boolean.class;
    }

    @Override
    public String toString(Boolean object) {
        return object ? "1" : "0";
    }

    @Override
    public Boolean fromString(String string) {
        if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")) {
            return "true".equalsIgnoreCase(string);
        }
        if (string.equalsIgnoreCase("1") || string.equalsIgnoreCase("0")) {
            return "1".equalsIgnoreCase(string);
        }
        return null;
    }
}
