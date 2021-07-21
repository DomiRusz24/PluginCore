package me.domirusz24.plugincore.util;

import me.domirusz24.plugincore.PluginCore;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface CompleteListener extends Listener {

    default void registerListener() {
        unregisterListener();
        Bukkit.getServer().getPluginManager().registerEvents(this, PluginCore.plugin);
    }

    default void unregisterListener() {
        HandlerList.unregisterAll(this);
    }



}
