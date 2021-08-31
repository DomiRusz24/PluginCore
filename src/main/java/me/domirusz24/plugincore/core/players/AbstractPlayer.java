package me.domirusz24.plugincore.core.players;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.displayable.CustomScoreboard;
import me.domirusz24.plugincore.core.placeholders.PlaceholderPlayer;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class AbstractPlayer implements PlaceholderPlayer {

    public static final ArrayList<AbstractPlayer> PLAYERS = new ArrayList<>();

    private boolean registered = true;

    protected final Player player;

    protected final BossBar bossBar;

    protected final CustomScoreboard scoreboard;

    private final ItemStack[] inventory;

    private final PluginCore plugin;

    public AbstractPlayer(PluginCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        bossBar = bossBar();
        scoreboard = scoreboard();
        if (bossBar != null) bossBar.addPlayer(player);
        if (scoreboard != null) {
            if (!overrideScoreBoard()) {
                if (!UtilMethods.hasScoreboard(player)) {
                    scoreboard.addPlayer(player);
                }
            } else {
                scoreboard.addPlayer(player);
            }
        }
        if (resetInventory()) {
            inventory = player.getInventory().getContents();
            player.getInventory().clear();
        } else {
            inventory = null;
        }
        PLAYERS.add(this);
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }

    public void toggleBoard(boolean toggle) {
        if (scoreboard != null) {
            if (toggle) {
                scoreboard.addPlayer(player);
            } else {
                scoreboard.removePlayer(player);
            }
        }
    }

    public void unregister() {
        if (registered) {
            if (bossBar != null) bossBar.removeAll();
            if (scoreboard != null) scoreboard.removePlayer(player);
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

    protected CustomScoreboard scoreboard() {
        return null;
    }

    protected boolean overrideScoreBoard() {
        return true;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public CustomScoreboard getScoreBoard() {
        return scoreboard;
    }
}
