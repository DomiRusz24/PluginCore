package me.domirusz24.plugincore.core.stringobject;

public class IntegerObj extends StringObject<Integer> {

    static {
        new IntegerObj(null);
    }

    private int min;
    private int max;

    public IntegerObj(String name, DefaultValue<Integer> defaultValue, int min, int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }

    public IntegerObj(String name, DefaultValue<Integer> defaultValue) {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerObj(String name) {
        this(name, null);
    }

    public IntegerObj(String name, int min, int max) {
        this(name, null, min, max);
    }


    @Override
    public Class<Integer> getClazz() {
        return int.class;
    }

    @Override
    public String toString(Integer object) {
        return String.valueOf(object);
    }

    @Override
    public Integer fromString(String string) {
        if (string == null) return null;
        try {
           int i = Integer.parseInt(string);
           if (i >= min && i <= max) {
               return i;
           }
           return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public StringObject<Integer> defaultObject() {
        return new IntegerObj(null);
    }

}
