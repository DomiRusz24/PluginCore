package me.domirusz24.plugincore.core.placeholders;

import me.domirusz24.plugincore.core.PluginInstance;

public interface PlaceholderObject extends PluginInstance {
    String onPlaceholderRequest(String param);

    String placeHolderPrefix();
}
