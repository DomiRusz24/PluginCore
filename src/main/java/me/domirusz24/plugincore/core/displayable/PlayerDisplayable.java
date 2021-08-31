package me.domirusz24.plugincore.core.displayable;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerDisplayable extends Displayable {

    private final List<Player> players = new ArrayList<>();

    public PlayerDisplayable(PluginCore pluginCore, PlaceholderObject... placeholders) {
        super(pluginCore, placeholders);
    }


    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            if (onPlayerAdd(player)) {
                players.add(player);
            }
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            if (onPlayerRemove(player)) {
                players.remove(player);
            }
        }
    }

    public void removeAllPlayers() {
        for (Player player : getPlayers()) {
            removePlayer(player);
        }
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    protected abstract boolean onPlayerAdd(Player player);

    protected abstract boolean onPlayerRemove(Player player);


}
