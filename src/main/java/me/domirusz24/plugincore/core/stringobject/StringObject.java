package me.domirusz24.plugincore.core.stringobject;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class StringObject<T> {

    public static <E> E getInstance(Class<E> clazz, List<String> args) {
        if (clazz == null) return null;
        if (clazz.getDeclaredConstructors().length != 0) {
            Class<?>[] parameterTypes = clazz.getDeclaredConstructors()[0].getParameterTypes();
            if (args.size() != parameterTypes.length) return null;
            Object[] objectArgs = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                StringObject<?> obj = StringObject.getStringObject(parameterTypes[i]);
                if (obj == null) return null;
                Object object = obj.fromString(args.get(i));
                if (object != null) {
                    objectArgs[i] = object;
                } else {
                    return null;
                }
            }
            try {
                return (E) clazz.getDeclaredConstructors()[0].newInstance(objectArgs);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return (E) clazz.getConstructors()[0].newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <E> E getInstance(Class<E> clazz, String[] args) {
        return getInstance(clazz, Arrays.asList(args));
    }

    private static final HashMap<Class<?>, StringObject<?>> STRING_OBJECTS = new HashMap<>();

    private final String name;

    private final DefaultValue<T> defaultValue;

    public StringObject(String name) {
        this(name, null);
    }

    public StringObject(String name, DefaultValue<T> defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
       if (STRING_OBJECTS.getOrDefault(getClazz(), null) == null) STRING_OBJECTS.put(getClazz(), this);
    }

    public abstract StringObject<T> defaultObject();

    public String getName() {
        return name;
    }

    public String getCustomFail(String s) {
        return null;
    }

    public abstract Class<T> getClazz();

    public abstract String toString(T object);

    public abstract T fromString(String string);

    public DefaultValue<T> getDefaultValue() {
        return defaultValue;
    }

    public interface DefaultValue<T> {
        T getDefaultValue(Object[] data);
    }

    public static  <E> StringObject<E> getStringObject(Class<E> clazz) {
        return (StringObject<E>) STRING_OBJECTS.getOrDefault(clazz, null);
    }
}
