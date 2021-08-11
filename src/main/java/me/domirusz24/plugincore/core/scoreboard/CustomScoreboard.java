package me.domirusz24.plugincore.core.scoreboard;

import com.projectkorra.projectkorra.BendingPlayer;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.util.CompleteListener;
import me.domirusz24.plugincore.util.PerTick;
import me.domirusz24.plugincore.util.UtilMethods;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

public abstract class CustomScoreboard extends AbstractPlayer implements CompleteListener {

    private final Scoreboard scoreboard;
    private final Objective objective;

    private int SPACE_AMOUNT = 1;

    private final HashMap<Integer, String> VALUES = new HashMap<>();

    public CustomScoreboard(Player player, String name, String display) {
        super(player);
        registerListener();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(name, "dummy", display);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        PluginCore.boardM.put(player, this);
    }

    public int offset() {
        return 0;
    }

    public void update() {
        onUpdate();
        updateScoreboard();
        player.setScoreboard(scoreboard);
    }

    private void updateScoreboard() {
        for (Integer index : VALUES.keySet()) {
            String name = getIndex(index);
            Team team = scoreboard.getTeam(name);
            if (team == null) {
                team = scoreboard.registerNewTeam(name);
                team.addEntry("§" + index);
                objective.getScore("§" + index).setScore((-index) + offset());
            }
            loadToScoreboard(VALUES.get(index), team);
        }
        for (Team team : scoreboard.getTeams()) {
            int index = getIndex(team.getName());
            if (!VALUES.containsKey(index)) {
                team.unregister();
            }
        }
    }

    private void loadToScoreboard(String row, Team team) {
        if (row.length() <= 16) {
            team.setPrefix(row);
            team.setSuffix("");
        } else {
            team.setPrefix(row.substring(0, 16));
            team.setSuffix(findColor(team.getPrefix()) + row.substring(16, Math.min(32, row.length())));
        }
    }

    protected String findColor(String prefix) {
        return UtilMethods.findColor(prefix);
    }

    protected void reset() {
        SPACE_AMOUNT = 0;
        set(0, getSpace());
    }

    protected abstract void onUpdate();

    protected abstract void onSlotChange(PlayerItemHeldEvent event);

    protected String getIndex(int index) {
        if (index < 0) index*=-1;
        return player.getUniqueId().toString().substring(0, 6) + "-" + index;
    }

    protected int getIndex(String index) {
        int i;
        try {
            i = Integer.parseInt(index.substring(7));
        } catch (Exception e) {
            i = -1;
        }
        return i;
    }

    protected void set(int index, String string) {
        if (index < 0) index*=-1;
        if (string == null) {
            VALUES.remove(index);
        } else {
            VALUES.put(index, string);
        }
    }

    private String getSpace() {
        SPACE_AMOUNT++;
        return StringUtils.repeat(" ", SPACE_AMOUNT);
    }


    @Override
    protected void onUnregister() {
        PluginCore.boardM.unregister(player);
        unregisterListener();
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @Override
    public boolean resetInventory() {
        return false;
    }

    @EventHandler
    public void onSlotChangeEvent(PlayerItemHeldEvent event) {
        if (!event.getPlayer().getName().equals(player.getName())) return;
        onSlotChange(event);
    }
}
