package me.domirusz24.plugincore.attributes;

import me.domirusz24.plugincore.core.players.PlayerData;
import me.domirusz24.plugincore.managers.database.DataBaseTable;
import me.domirusz24.plugincore.managers.database.DatabaseObject;
import org.bukkit.Bukkit;

import java.util.HashMap;
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
                    main.getCorePlugin().log(Level.INFO, "Created player data for " + name + "!");
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
            readyToAccess = true;
            main.getCorePlugin().log(Level.INFO, "Loaded player data for " + getStringValue("name") + "!");
            Bukkit.getScheduler().runTask(main.getCorePlugin(), main::sqlLoad);
        });
    }

    public String getStringValue(String key) {
        checkIfReady();
        return (String) VALUES.getOrDefault(key, null);
    }

    public int getIntegerValue(String key) {
        checkIfReady();
        return (int) VALUES.getOrDefault(key, null);
    }

    public boolean getBooleanValue(String key) {
        checkIfReady();
        if (!VALUES.containsKey(key)) return false;
        return VALUES.get(key).equals(1);
    }

    private void setToHashMap(String key, Object value) {
        VALUES.put(key, value);
    }

    public void setStringValue(String key, String value) {
        checkIfReady();
        setToHashMap(key, value);
        setStringField(key, value);
    }

    public void setIntegerValue(String key, int value) {
        checkIfReady();
        setToHashMap(key, value);
        setIntegerField(key, value);
    }

    public void setBooleanValue(String key, boolean value) {
        checkIfReady();
        setToHashMap(key, value ? 1 : 0);
        setBooleanField(key, value);
    }

    private boolean readyToAccess = false;

    private void checkIfReady() {
        if (!readyToAccess) {
            throw new IllegalArgumentException("SQL " + getIndex() + " isn't ready yet!");
        }
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
