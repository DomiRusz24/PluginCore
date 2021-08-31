package me.domirusz24.plugincore.core.players.glide;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerCommandAction extends GlideAction {

    private final String command;

    public PlayerCommandAction(Location to, String command) {
        super(to);
        this.command = command.replaceAll("_", " ");
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String name() {
        return "command";
    }

    @Override
    protected ActionProgress run(Player player) {
        return new ActionProgress(this, player) {
            @Override
            protected void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), getCommand().replaceAll("%player%", player.getName()));
                stop();
            }
        };
    }
}
