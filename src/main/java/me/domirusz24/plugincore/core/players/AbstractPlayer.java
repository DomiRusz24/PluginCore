package me.domirusz24.plugincore.core.players;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class AbstractPlayer {

    public static final ArrayList<AbstractPlayer> PLAYERS = new ArrayList<>();

    private boolean registered = true;

    protected final Player player;

    protected final BossBar bossBar;

    private final ItemStack[] inventory;

    public AbstractPlayer(Player player) {
        this.player = player;
        bossBar = bossBar();
        if (bossBar != null) bossBar.addPlayer(player);
        if (resetInventory()) {
            inventory = player.getInventory().getContents();
            player.getInventory().clear();
        } else {
            inventory = null;
        }
        PLAYERS.add(this);
    }

    public void unregister() {
        if (registered) {
            if (bossBar != null) bossBar.removeAll();
            if (resetInventory()) player.getInventory().setContents(inventory);
            PLAYERS.remove(this);
            onUnregister();
            registered = false;
        }
    }

    protected abstract void onUnregister();

    public Player getPlayer() {
        return player;
    }

    public abstract boolean resetInventory();

    protected BossBar bossBar() {
        return null;
    }

    public BossBar getBossBar() {
        return bossBar;
    }
}
