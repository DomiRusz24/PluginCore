package me.domirusz24.plugincore.attributes;

public class Attribute<T extends AttributeBase<? extends AttributeAble>> {

    private final String name;

    private final Class<T> clazz;

    public Attribute(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public Class<T> getBaseClass() {
        return clazz;
    }

    public String getName() {
        return name;
    }
}
