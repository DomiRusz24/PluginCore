package me.domirusz24.plugincore.core.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.domirusz24.plugincore.PluginCore;
import org.bukkit.entity.Player;

public interface PlaceholderPlayer extends PlaceholderObject {
    Player getPlayer();

    @Override
    default String onPlaceholderRequest(String param) {
        return PlaceholderAPI.setPlaceholders(getPlayer(), param);
    }

    @Override
    default String placeHolderPrefix() {
        return getCorePlugin().getName().toLowerCase();
    }
}
