package me.domirusz24.plugincore.core.stringobject;

public class DoubleObj extends StringObject<Double> {

    static {
        new DoubleObj(null);
    }

    public DoubleObj(String name, DefaultValue<Double> defaultValue) {
        super(name, defaultValue);
    }

    public DoubleObj(String name) {
        this(name, null);
    }

    @Override
    public Class<Double> getClazz() {
        return double.class;
    }

    @Override
    public String toString(Double object) {
        return String.valueOf(object);
    }

    @Override
    public Double fromString(String string) {
        if (string == null) return null;
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public StringObject<Double> defaultObject() {
        return new DoubleObj(null);
    }
}
