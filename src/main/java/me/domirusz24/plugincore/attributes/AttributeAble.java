package me.domirusz24.plugincore.attributes;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public abstract class AttributeAble {

    private HashMap<Attribute<? extends AttributeBase<?>>, AttributeBase<?>> ATTRIBUTES = new HashMap<>();

    private  <E extends AttributeAble, T extends AttributeBase<E>> T createInstance(Attribute<T> attribute, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor(this.getClass(), Attribute.class);
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
