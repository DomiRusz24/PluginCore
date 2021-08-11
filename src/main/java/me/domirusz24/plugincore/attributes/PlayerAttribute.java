package me.domirusz24.plugincore.attributes;

public class PlayerAttribute<T extends AttributeBase<? extends AttributeAble>> extends Attribute<T> {

    public static final PlayerAttribute<SQLAttribute> SQL = new PlayerAttribute<>("sql", SQLAttribute.class, true);

    private final boolean offline;

    public PlayerAttribute(String name, Class<T> clazz, boolean offline) {
        super(name, clazz);
        this.offline = offline;
    }

    public PlayerAttribute(String name, Class<T> clazz) {
        this(name, clazz, false);
    }

    public boolean isOffline() {
        return offline;
    }
}
