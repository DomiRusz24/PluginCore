package me.domirusz24.plugincore.util;

import me.domirusz24.plugincore.core.PluginInstance;

public interface PerTick extends PluginInstance {
    default void registerPerTick() {
        unregisterPerTick();
        getCorePlugin().listener.hookInListener(this);
    }

    default void unregisterPerTick() {
        getCorePlugin().listener.removeListener(this);
    }

    void onTick();
}
