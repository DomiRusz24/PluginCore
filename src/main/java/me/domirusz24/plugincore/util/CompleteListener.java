package me.domirusz24.plugincore.util;

import me.domirusz24.plugincore.core.PluginInstance;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface CompleteListener extends Listener, PluginInstance {

    default void registerListener() {
        unregisterListener();
        Bukkit.getServer().getPluginManager().registerEvents(this, getCorePlugin());
    }

    default void unregisterListener() {
        HandlerList.unregisterAll(this);
    }



}
