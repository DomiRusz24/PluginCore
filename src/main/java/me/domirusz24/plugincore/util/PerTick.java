package me.domirusz24.plugincore.util;

import me.domirusz24.plugincore.CoreListener;
import me.domirusz24.plugincore.PluginCore;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public interface PerTick {
    default void registerPerTick() {
        unregisterPerTick();
        CoreListener.hookInListener(this);
    }

    default void unregisterPerTick() {
        CoreListener.removeListener(this);
    }

    void onTick();
}
