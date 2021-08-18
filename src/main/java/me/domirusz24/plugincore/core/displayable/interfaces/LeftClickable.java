package me.domirusz24.plugincore.core.displayable.interfaces;

import me.domirusz24.plugincore.core.PluginInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface LeftClickable extends PluginInstance {
    void onLeftClick(Player player);

    boolean isLeftClickedOn(PlayerInteractEvent event);

    boolean isLeftClickedOn(EntityDamageByEntityEvent event);
}
