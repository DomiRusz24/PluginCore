package me.domirusz24.plugincore.core.displayable;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.managers.PAPIManager;
import me.domirusz24.plugincore.util.UtilMethods;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;

public class CustomScoreboard extends PlayerDisplayable {

    private String title;

    private final String name;

    private int SPACE_AMOUNT = 1;

    private final String uuid;

    private Objective objective;

    private final Scoreboard scoreboard;

    public CustomScoreboard(PluginCore plugin, String name, String title) {
        this(plugin, name, title, new PlaceholderObject[0]);
    }

    public CustomScoreboard(PluginCore plugin, String name, String title, PlaceholderObject... objects) {
        super(plugin, objects);
        uuid = UUID.randomUUID().toString().substring(0, 6);
        this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        this.name = name.substring(0, Math.min(name.length(), 16));
        this.title = title;
        objective = scoreboard.registerNewObjective(this.name, "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        updateTitle();
    }

    public int offset() {
        return 0;
    }

    @Override
    protected void onUpdate(ArrayList<String> values) {
        updateTitle();
        for (int index = 0; index < values.size(); index++) {
            String name = getIndex(index);
            Team team = scoreboard.getTeam(name);
            if (team == null) {
                team = scoreboard.registerNewTeam(name);
                team.addEntry("§" + index);
                objective.getScore("§" + index).setScore((-index) + offset());
            }
            loadToScoreboard(values.get(index), team);
        }
        for (Player player : getPlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    private void updateTitle() {
        String title = this.title;
        for (PlaceholderObject placeholder : getPlaceholders()) {
            title = PAPIManager.setPlaceholders(plugin, placeholder, title);
        }
        objective.setDisplayName(title);
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
        addValue(0, getSpace());
    }

    protected String getIndex(int index) {
        if (index < 0) index*=-1;
        return uuid + "-" + index;
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

    private String getSpace() {
        SPACE_AMOUNT++;
        return StringUtils.repeat(" ", SPACE_AMOUNT);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    protected boolean onPlayerAdd(Player player) {
        plugin.boardM.put(player, this);
        player.setScoreboard(scoreboard);
        return true;
    }

    @Override
    protected boolean onPlayerRemove(Player player) {
        plugin.boardM.unregister(player);
        player.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
        return true;
    }
}
