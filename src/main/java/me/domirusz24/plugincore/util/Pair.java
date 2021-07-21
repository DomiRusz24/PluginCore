package me.domirusz24.plugincore.util;

public class Pair<T, U> {

    private final T key;
    private final U value;

    public Pair(T key, U value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public U getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Pair) {
            return ((Pair<?, ?>) obj).key.equals(key);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "{Pair=" + getKey().toString() + "," + getValue().toString() + "}";
    }
}
