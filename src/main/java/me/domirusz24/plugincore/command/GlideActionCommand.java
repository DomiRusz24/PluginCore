package me.domirusz24.plugincore.command;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.abstractclasses.BaseSubCommand;
import me.domirusz24.plugincore.core.players.glide.GlideAction;
import me.domirusz24.plugincore.util.UtilMethods;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

public class GlideActionCommand extends BaseSubCommand {

    private final PluginCore plugin;

    public GlideActionCommand(PluginCore plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, List<String> args) {
        if (!isPlayer(sender)) return;
        if (args.size() != 0) {
            if (GlideAction.getClassAction(args.get(0)) != null) {
                String loc = UtilMethods.locationToString(((Player) sender).getLocation(), true);
                String[] arg = (String[]) ArrayUtils.add(new String[]{loc}, args.subList(1, args.size()).toArray());
                GlideAction action = GlideAction.getAction(args.get(0), arg);
            }
        }
        sender.sendMessage(Languages.FAIL);
    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return new ArrayList<>(GlideAction.getActions());
        }
        return new ArrayList<>();
    }

    @Override
    protected String name() {
        return null;
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected String description() {
        return null;
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
