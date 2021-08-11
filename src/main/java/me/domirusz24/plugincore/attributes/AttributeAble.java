package me.domirusz24.plugincore.attributes;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;

public abstract class AttributeAble {

    private final Class<? extends AttributeAble> mainClass;

    public AttributeAble() {
        mainClass = this.getClass();
    }

    public AttributeAble(Class<? extends AttributeAble> mainClass) {
        this.mainClass = mainClass;
    }

    private HashMap<Attribute<? extends AttributeBase<?>>, AttributeBase<?>> ATTRIBUTES = new HashMap<>();

    private  <E extends AttributeAble, T extends AttributeBase<E>> T createInstance(Attribute<T> attribute, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(mainClass, Attribute.class);
            return constructor.newInstance(this, attribute);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public <E extends AttributeAble, T extends AttributeBase<E>> T getAttribute(Attribute<T> attribute) {
        if (!ATTRIBUTES.containsKey(attribute)) {
            try {
                ATTRIBUTES.put(attribute, createInstance(attribute, attribute.getBaseClass()));
            } catch (Exception ignored){}
        }
        return attribute.getBaseClass().cast(ATTRIBUTES.getOrDefault(attribute, null));
    }

    public <E extends AttributeAble, T extends AttributeBase<E>> boolean removeAttribute(Attribute<T> attribute) {
        if (ATTRIBUTES.containsKey(attribute)) {
            ATTRIBUTES.remove(attribute);
            return true;
        } else {
            return false;
        }
    }

    public HashMap<Attribute<? extends AttributeBase<?>>, AttributeBase<?>> getAttributes() {
        return new HashMap<>(ATTRIBUTES);
    }
}
