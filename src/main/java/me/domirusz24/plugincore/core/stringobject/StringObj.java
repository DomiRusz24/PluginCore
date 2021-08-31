package me.domirusz24.plugincore.core.stringobject;

public class StringObj extends StringObject<String> {

    static {
        new StringObj(null);
    }

    private final int min;
    private final int max;

    public StringObj(String name, DefaultValue<String> defaultValue, int min, int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }

    public StringObj(String name, DefaultValue<String> defaultValue) {
        this(name, defaultValue, 0, -1);
    }

    public StringObj(String name) {
        this(name, null);
    }

    public StringObj(String name, int min, int max) {
        this(name, null, min, max);
    }


    @Override
    public Class<String> getClazz() {
        return String.class;
    }

    @Override
    public String toString(String object) {
        return object;
    }

    @Override
    public String fromString(String string) {
        if (string.length() > min) {
            if (max == -1 || string.length() <= max) {
                return string;
            }
        }
        return null;
    }

    @Override
    public StringObject<String> defaultObject() {
        return new StringObj(null);
    }
}
