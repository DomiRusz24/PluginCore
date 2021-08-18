package me.domirusz24.plugincore.core.displayable.interfaces;

import me.domirusz24.plugincore.core.PluginInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface RightClickable extends PluginInstance {
    void onRightClick(Player player);

    boolean isRightClickedOn(PlayerInteractEvent event);

    boolean isRightClickedOn(PlayerInteractAtEntityEvent event);
}
