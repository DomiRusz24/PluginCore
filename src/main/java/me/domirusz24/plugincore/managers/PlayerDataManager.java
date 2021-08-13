package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.players.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager extends Manager {
    private HashMap<UUID, PlayerData> PLAYER_BY_UUID = new HashMap<>();

    public PlayerDataManager(PluginCore plugin) {
        super(plugin);
    }

    public void register(PlayerData player) {
        PLAYER_BY_UUID.put(player.getUuid(), player);
    }

    public PlayerData getPlayer(String name, UUID uuid) {
        if (!PLAYER_BY_UUID.containsKey(uuid)) {
            PlayerData p = plugin.registerPlayer(name, uuid);
            if (p != null) {
                register(p);
            }
            return null;
        }
        return PLAYER_BY_UUID.get(uuid);
    }

    public PlayerData getPlayer(Player player) {
        return getPlayer(player.getName(), player.getUniqueId());
    }

    public boolean exists(UUID uuid) {
        return PLAYER_BY_UUID.containsKey(uuid);
    }

    public void unregister(PlayerData player) {
        PLAYER_BY_UUID.remove(player.getUuid());
    }
}
