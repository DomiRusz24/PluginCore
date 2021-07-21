package me.domirusz24.plugincore.config;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.ConfigManager;
import me.domirusz24.plugincore.util.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

public class PhantomSchematics extends AbstractConfig {
    public PhantomSchematics(File file, PluginCore plugin, ConfigManager manager) {
        super(file, plugin, manager);
    }

    public PhantomSchematics(String path, PluginCore plugin, ConfigManager manager) {
        super(path, plugin, manager);
    }

    public PhantomSchematics(String path, PluginCore plugin) {
        super(path, plugin);
    }

    public PhantomSchematics(File file, PluginCore plugin) {
        super(file, plugin);
    }

    public void addSchematic(World world, int x, int z, UUID uuid, String name, Boolean autoRemove) {
        HashSet<String> list = new HashSet<>(getStringList("Player." + uuid.toString() + "." + world.getName() + "." + x + "." + z));

        list.add(schematicToString(new Pair<>(name, autoRemove)));

        set("Player." + uuid.toString() + "." + world.getName() + "." + x + "." + z, new ArrayList<>(list));
    }

    public String schematicToString(Pair<String, Boolean> pair) {
        return pair.getKey() + ";" + pair.getValue().toString();
    }

    public void removeSchematic(World world, int x, int z, UUID uuid, String name) {
        Set<Pair<String, Boolean>> schemList = getSchematics(world, x, z, uuid);
        schemList.removeIf(single -> single.getKey().equals(name));
        if (schemList.isEmpty()) {
            set("Player." + uuid.toString() + "." + world.getName() + "." + x + "." + z, null);
        } else {
            set("Player." + uuid.toString() + "." + world.getName() + "." + x + "." + z, name);
        }
    }

    public Set<Pair<String, Boolean>> getSchematics(World world, int x, int z, UUID uuid) {
        if (!isSet("Player." + uuid.toString() + "." + world.getName() + "." + x + "." + z)) {
            return null;
        }
        HashSet<Pair<String, Boolean>> set = new HashSet<>();
        for (String s : getStringList("Player." + uuid.toString() + "." + world.getName() + "." + x + "." + z)) {
            String[] split = s.split(";");
            set.add(new Pair<>(split[0], Boolean.getBoolean(split[1])));
        }
        return set;
    }

    public Map<Chunk, Set<Pair<String, Boolean>>> getSchematics(UUID uuid) {
        ConfigurationSection player = getConfigurationSection("Player." + uuid.toString());
        if (player == null) return new HashMap<>();
        HashMap<Chunk, Set<Pair<String, Boolean>>> schematics = new HashMap<>();

        for (String worldString : player.getKeys(false)) {
            World world = AbstractConfig.getWorld(worldString);
            if (world == null) continue;
            ConfigurationSection worldSection = player.getConfigurationSection(worldString);

            for (String xString : worldSection.getKeys(false)) {
                int x = Integer.parseInt(xString);
                ConfigurationSection xSection = worldSection.getConfigurationSection(xString);

                for (String zString : xSection.getKeys(false)) {
                    int z = Integer.parseInt(zString);
                    Chunk chunk = world.getChunkAt(new Location(world, x * 16, 10, z * 16));
                    schematics.put(chunk, getSchematics(world, x, z, uuid));
                }
            }
        }
        return schematics;
    }
}
