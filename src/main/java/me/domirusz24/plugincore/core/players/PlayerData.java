package me.domirusz24.plugincore.core.players;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.attributes.AttributeAble;
import me.domirusz24.plugincore.attributes.AttributeBase;
import me.domirusz24.plugincore.attributes.PlayerAttribute;
import me.domirusz24.plugincore.core.placeholders.PlaceholderPlayer;
import me.domirusz24.plugincore.managers.database.DataBaseTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PlayerData extends AttributeAble implements PlaceholderPlayer {
    private boolean sqlLoaded = false;
    protected final UUID uuid;
    protected final String name;

    private final List<Runnable> onSqlLoad = new ArrayList<>();

    private Player player;

    private final PluginCore plugin;

    public PlayerData(PluginCore plugin, String name, UUID uuid) {
        super(PlayerData.class);
        this.plugin = plugin;
        this.uuid = uuid;
        this.name = name;
        loadAttributes();
    }

    public void onSqlLoad(Runnable runnable) {
        if (sqlLoaded) {
            runnable.run();
        } else {
            onSqlLoad.add(runnable);
        }
    }

    private void loadAttributes() {
        player = Bukkit.getPlayer(uuid);
        if (getPlayer() == null) {
            getAttribute(PlayerAttribute.SQL).load(name);
        } else {
            getAttribute(PlayerAttribute.SQL).load(getPlayer().getName());
        }
    }

    public boolean isSqlLoaded() {
        return sqlLoaded;
    }

    public void sqlLoad() {
        this.sqlLoaded = true;
        for (Runnable run : onSqlLoad) {
            run.run();
        }
        onSqlLoad.clear();
        onSqlLoad();
        if (getPlayer() != null) onPlayerJoin();
    }

    protected abstract void onSqlLoad();

    protected abstract void onPlayerJoin();

    public abstract DataBaseTable getTable();


    public void onJoin() {
        player = Bukkit.getPlayer(uuid);
        onPlayerJoin();
    }

    public void onLeave() {
        for (AttributeBase<?> base : new ArrayList<>(getAttributes().values())) {
            base.unregister();
        }
        player = null;
        plugin.playerDataM.unregister(this);
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
