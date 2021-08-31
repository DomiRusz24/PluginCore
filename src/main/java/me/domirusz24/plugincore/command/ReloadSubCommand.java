package me.domirusz24.plugincore.command;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.abstractclasses.BaseSubCommand;
import me.domirusz24.plugincore.config.annotations.Language;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends BaseSubCommand {

    private final PluginCore plugin;

    public ReloadSubCommand(PluginCore plugin) {
        this.plugin = plugin;
    }

    @Language("Command.Reload.Description")
    public static String LANG_DESCRIPTION = "Allows you to reload the configs";

    @Override
    protected void execute(CommandSender sender, List<String> args) {
        if (plugin.configM.reloadConfigs()) {
            sender.sendMessage(ChatColor.GREEN + "Success!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failure!");
        }
    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>();
    }

    @Override
    protected String name() {
        return "reload";
    }

    @Override
    protected String usage() {
        return "reload";
    }

    @Override
    protected String description() {
        return LANG_DESCRIPTION;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return null;
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
