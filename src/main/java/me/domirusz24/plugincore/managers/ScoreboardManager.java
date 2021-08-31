package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.displayable.CustomScoreboard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ScoreboardManager extends Manager {

    private final HashMap<UUID, CustomScoreboard> SCOREBOARD_BY_UUID = new HashMap<>();

    public ScoreboardManager(PluginCore plugin) {
        super(plugin);
    }

    public CustomScoreboard get(Player player) {
        return SCOREBOARD_BY_UUID.get(player.getUniqueId());
    }

    public void put(Player player, CustomScoreboard board) {
        if (SCOREBOARD_BY_UUID.containsKey(player.getUniqueId())) {
            SCOREBOARD_BY_UUID.get(player.getUniqueId()).removePlayer(player);
        }
        SCOREBOARD_BY_UUID.put(player.getUniqueId(), board);
    }

    public void unregister(Player player) {
        SCOREBOARD_BY_UUID.remove(player.getUniqueId());
    }
}
