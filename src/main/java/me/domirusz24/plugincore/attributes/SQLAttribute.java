package me.domirusz24.plugincore.attributes;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.players.PlayerData;
import me.domirusz24.plugincore.managers.database.DataBaseTable;
import me.domirusz24.plugincore.managers.database.DatabaseObject;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class SQLAttribute extends AttributeBase<PlayerData> implements DatabaseObject {
    public HashMap<String, Object> VALUES = new HashMap<>();

    public SQLAttribute(PlayerData main, Attribute<? extends AttributeBase<PlayerData>> attribute) {
        super(main, attribute);
    }

    public void load(String name) {
        exists((b) -> {
            if (!b) {
                putDefault(() -> {
                    PluginCore.plugin.log(Level.INFO, "Created player data for " + name + "!");
                    loadDataFromSQL(name);
                });
            } else {
                loadDataFromSQL(name);
            }
        });
    }

    private void loadDataFromSQL(String name) {
        setStringField( "name", name);
        getHashMapIndex((p) -> {
            for (String s : p.keySet()) {
                VALUES.put(s, p.get(s));
            }
            PluginCore.plugin.log(Level.INFO, "Loaded player data for " + getStringValue("name") + "!");
            Bukkit.getScheduler().runTask(PluginCore.plugin, main::sqlLoad);
        });
    }

    public String getStringValue(String key) {
        return (String) VALUES.getOrDefault(key, null);
    }

    public int getIntegerValue(String key) {
        return (int) VALUES.getOrDefault(key, null);
    }

    public boolean getBooleanValue(String key) {
        if (!VALUES.containsKey(key)) return false;
        return VALUES.get(key).equals(1);
    }

    private void setToHashMap(String key, Object value) {
        VALUES.put(key, value);
    }

    public void setStringValue(String key, String value) {
        setToHashMap(key, value);
        setStringField(key, value);
    }

    public void setIntegerValue(String key, int value) {
        setToHashMap(key, value);
        setIntegerField(key, value);
    }

    public void setBooleanValue(String key, boolean value) {
        setToHashMap(key, value ? 1 : 0);
        setBooleanField(key, value);
    }

    @Override
    public void onUnregister() {}

    @Override
    public DataBaseTable getTable() {
        return main.getTable();
    }

    @Override
    public String getIndex() {
        return getMain().getUuid().toString();
    }
}
