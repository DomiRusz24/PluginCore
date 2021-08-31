package me.domirusz24.plugincore.config;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.ConfigManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;

public class SchematicLocationsConfig extends AbstractConfig {

    private HashMap<String, Location> SCHEMATIC_MINS = null;

    public SchematicLocationsConfig(String path, PluginCore plugin, ConfigManager manager) {
        super(path, plugin, manager);
    }

    public SchematicLocationsConfig(String path, PluginCore plugin) {
        super(path, plugin);
    }

    public SchematicLocationsConfig(File file, PluginCore plugin) {
        super(file, plugin);
    }

    public SchematicLocationsConfig(File file, PluginCore plugin, ConfigManager manager) {
        super(file, plugin, manager);
    }



    @Override
    public void _reload() {
        if (SCHEMATIC_MINS == null) {
            SCHEMATIC_MINS = new HashMap<>();
        }
        ConfigurationSection section = getConfigurationSection("MinLocations");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key == null) continue;
                Location loc = getLocation("MinLocations." + key);
                if (loc != null) {
                    SCHEMATIC_MINS.put(key, loc);
                }
            }
        }
    }

    public String getNonDuplicateId(String Id) {
        if (SCHEMATIC_MINS.containsKey(Id)) {
            int i = 1;
            while (SCHEMATIC_MINS.containsKey(Id + "_" + i)) {
                i++;
            }
            return Id + "_" + i;
        }
        return Id;
    }

    public Location getMin(String ID) {
        if (SCHEMATIC_MINS == null) SCHEMATIC_MINS = new HashMap<>();
        return SCHEMATIC_MINS.getOrDefault(ID, null);
    }

    public void setMin(String Id, Location location) {
        SCHEMATIC_MINS.put(Id, location);
        setLocation("MinLocations." + Id, location);
        save();
    }
}
