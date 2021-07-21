package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;

public class Manager {

    protected PluginCore plugin;

    public Manager(PluginCore plugin) {
        this.plugin = plugin;
    }

    public PluginCore getPlugin() {
        return plugin;
    }
}
