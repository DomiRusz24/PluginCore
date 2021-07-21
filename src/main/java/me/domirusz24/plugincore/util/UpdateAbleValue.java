package me.domirusz24.plugincore.util;

import java.util.Objects;
import java.util.function.Consumer;

public class UpdateAbleValue<T> {

    private T value = null;

    private Consumer<T> onUpdate;

    public UpdateAbleValue(T value, Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
        setValue(value);
    }

    public UpdateAbleValue(T value) {
        this(value, (p)->{});
    }

    public void setOnUpdate(Consumer<T> consumer) {
        this.onUpdate = consumer;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (!Objects.equals(this.value,value)) {
            this.value = value;
            onUpdate.accept(value);
        }
    }

    public void setObjectValue(Object value) {
        setValue((T) value);
    }

    public void update() {
        onUpdate.accept(value);
    }

}
