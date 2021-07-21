package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.region.CustomRegion;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

public class RegionManager extends Manager {
    private final HashMap<String, CustomRegion> Region_BY_ID = new HashMap<>();

    public Collection<CustomRegion> getRegions() {
        return Region_BY_ID.values();
    }

    private int tick = 0;

    public RegionManager(PluginCore plugin) {
        super(plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                tick++;
                getRegions().forEach((r) -> {
                    if (tick % r.delay() == 0)
                    r.updatePlayers();
                });
            }
        }.runTaskTimer(plugin, 20, 1);
    }

    public void addRegion(CustomRegion Region) {
        Region_BY_ID.put(Region.getID(), Region);
    }

    public CustomRegion getRegion(String ID) {
        return Region_BY_ID.getOrDefault(ID, null);
    }
}
