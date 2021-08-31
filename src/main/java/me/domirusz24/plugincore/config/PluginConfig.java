package me.domirusz24.plugincore.config;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.ConfigManager;

public class PluginConfig extends AbstractConfig {

    public PluginConfig(String path, PluginCore plugin, ConfigManager manager) {
        super(path, plugin, manager);
        save();
    }

    @Override
    protected boolean autoGenerate() {
        return true;
    }
}
