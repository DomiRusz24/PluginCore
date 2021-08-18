package me.domirusz24.plugincore.util;

import me.domirusz24.plugincore.CoreListener;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.PluginInstance;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public interface PerTick extends PluginInstance {
    default void registerPerTick() {
        unregisterPerTick();
        getCorePlugin().listener.removeListener(this);
    }

    default void unregisterPerTick() {
        getCorePlugin().listener.removeListener(this);
    }

    void onTick();
}
