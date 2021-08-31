package me.domirusz24.plugincore.command.abstractclasses;

import me.domirusz24.plugincore.core.PluginInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

import static me.domirusz24.plugincore.command.Languages.*;

public abstract class Command implements PluginInstance {

    protected abstract String name();
    protected abstract String usage();
    protected abstract String description();
    protected List<String> aliases() {
        return new ArrayList<>();
    }

    public String getName() {
        return this.name();
    }

    public String getUsage() {
        return this.usage();
    }

    public String getDescription() {
        return this.description();
    }

    public List<String> getAliases() {
        return this.aliases();
    }

    public boolean hideCommand() {
        return false;
    }

    public void help(CommandSender sender, boolean description) {
        sender.sendMessage(USAGE + getUsage());
        if (description) {
            sender.sendMessage(COMMAND_DESCRIPTION + description());
        }
    }

    public abstract PermissionDefault getPermissionDefault();

    public String getPermission() {
        return getCorePlugin().getName().toLowerCase() + ".command." + this.name().toLowerCase();
    }

    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission(getPermission())) {
            return true;
        } else {
            sender.sendMessage(INSUFFICIENT_PERMS);
            return false;
        }
    }

    public boolean hasPermission(CommandSender sender, String extra) {
        String permission = getPermission() + "." + extra;
        if (sender.hasPermission(permission)) {
            return true;
        } else {
            sender.sendMessage(INSUFFICIENT_PERMS);
            return false;
        }
    }

    protected boolean correctLength(CommandSender sender, int size, int min, int max) {
        if (size >= min && size <= max) {
            return true;
        } else {
            this.help(sender, false);
            return false;
        }
    }

    protected boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            sender.sendMessage(MUST_BE_PLAYER);
            return false;
        }
    }

    protected Integer getNumber(String string) {
        Integer value;
        try {
            value = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            value = null;
        }
        return value;
    }
}
