package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.Languages;
import me.domirusz24.plugincore.command.abstractclasses.AbstractCommand;
import me.domirusz24.plugincore.command.abstractclasses.AbstractSubCommand;
import me.domirusz24.plugincore.command.abstractclasses.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CommandManager extends Manager implements CommandExecutor, TabExecutor {

    private final HashSet<AbstractCommand<? extends AbstractSubCommand>> COMMANDS = new HashSet<>();

    public CommandManager(PluginCore plugin) {
        super(plugin);
        plugin.getDataFolder().mkdirs();
    }

    public void registerCommand(AbstractCommand<? extends AbstractSubCommand> command) {
        COMMANDS.add(command);
        plugin.getCommand(command.getName()).setExecutor(this);
        plugin.getCommand(command.getName()).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        String name = command.getName();
        List<String> args = Arrays.asList(strings);
        for (AbstractCommand<?> com : COMMANDS) {
            if (com.getName().equalsIgnoreCase(name) || (com.getAliases() != null && com.getAliases().contains(name.toLowerCase()))) {
                try {
                    if (args.size() == 1 && args.get(0).equalsIgnoreCase("help")) {
                        com.help(sender, true);
                    } else if (com.canSelfExecute(sender, args)) {
                        com.selfExecute(sender, args);
                    } else if (com.canExecuteSubCommands(sender, args)) {
                        for (AbstractSubCommand subCommand : com.getSubCommands()) {
                            if (subCommand.matching(args)) {
                                subCommand.executeCommand(sender, args);
                                return true;
                            }
                        }
                        com.help(sender, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Languages.FAIL_ERROR);
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        String name = command.getName();
        for (AbstractCommand<? extends AbstractSubCommand> com : COMMANDS) {
            if (com.getName().equalsIgnoreCase(name) || (com.getAliases() != null && com.getAliases().contains(name.toLowerCase()))) {
                return com.autoComplete(commandSender, Arrays.asList(strings));
            }
        }
        return null;
    }
}
