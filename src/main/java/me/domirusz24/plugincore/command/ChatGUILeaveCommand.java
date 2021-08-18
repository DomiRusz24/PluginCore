package me.domirusz24.plugincore.command;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.abstractclasses.BaseSubCommand;
import me.domirusz24.plugincore.config.annotations.Language;
import me.domirusz24.plugincore.core.chatgui.ChatGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class ChatGUILeaveCommand extends BaseSubCommand {

    private final PluginCore plugin;

    public ChatGUILeaveCommand(PluginCore plugin) {
        this.plugin = plugin;
    }

    @Language("Command.Panel.Description")
    public static String LANG_DESCRIPTION = "Panel control";

    @Override
    protected void execute(CommandSender sender, List<String> args) {
        if (isPlayer(sender)) {
            ChatGUI gui = plugin.chatGuiM.getChatGUI((Player) sender);
            if (gui != null) {
                gui.unregister();
            }
        }
    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        return null;
    }

    @Override
    protected String name() {
        return "panel";
    }

    @Override
    protected String usage() {
        return "panel";
    }

    @Override
    protected String description() {
        return LANG_DESCRIPTION;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.TRUE;
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
