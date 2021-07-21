package me.domirusz24.plugincore.attributes;

public abstract class AttributeBase<T extends AttributeAble> {

    protected final T main;

    protected final Attribute<? extends AttributeBase<T>> attribute;

    public AttributeBase(T main, Attribute<? extends AttributeBase<T>> attribute) {
        this.main = main;
        this.attribute = attribute;
    }

    public T getMain() {
        return main;
    }

    public void unregister() {
        main.removeAttribute(attribute);
        onUnregister();
    }

    public abstract void onUnregister();

}
