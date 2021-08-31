package me.domirusz24.plugincore.managers;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.Languages;
import me.domirusz24.plugincore.command.abstractclasses.AbstractCommand;
import me.domirusz24.plugincore.command.abstractclasses.AbstractSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CommandManager extends Manager implements TabExecutor {

    private static SimpleCommandMap COMMAND_MAP;
    private static List<Permission> PERMISSIONS_LIST;
    private static Constructor<PluginCommand> PLUGIN_COMMAND_CONSTRUCTOR;

    private final HashSet<AbstractCommand<? extends AbstractSubCommand>> COMMANDS = new HashSet<>();

    private final Permission parentPermission;

    public CommandManager(PluginCore plugin) {
        super(plugin);
        COMMAND_MAP = null;
        PERMISSIONS_LIST = null;
        PLUGIN_COMMAND_CONSTRUCTOR = null;
        try{
            Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            cmdMap.setAccessible(true);
            COMMAND_MAP = (SimpleCommandMap) cmdMap.get(Bukkit.getPluginManager());

            Field permList = PluginDescriptionFile.class.getDeclaredField("permissions");
            permList.setAccessible(true);
            permList.set(plugin.getDescription(), new ArrayList<>());
            PERMISSIONS_LIST = (List<Permission>) permList.get(plugin.getDescription());

            PLUGIN_COMMAND_CONSTRUCTOR = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            PLUGIN_COMMAND_CONSTRUCTOR.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        parentPermission = new Permission(plugin.getName() + ".command", PermissionDefault.OP);
        PERMISSIONS_LIST.add(parentPermission);
    }

    public void addPermission(String permission, PermissionDefault permissionDefault) {
        PERMISSIONS_LIST.add(new Permission(permission, permissionDefault));
    }

    public void registerCommand(AbstractCommand<? extends AbstractSubCommand> command) {
        COMMANDS.add(command);
        PluginCommand com = null;
        try {
            com = PLUGIN_COMMAND_CONSTRUCTOR.newInstance(command.getName().toLowerCase(), plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        com.setAliases(command.getAliases());
        com.setUsage(command.getUsage());
        com.setDescription(command.getDescription());
        com.setName(command.getName().toLowerCase());
        com.setLabel(command.getName().toLowerCase());

        com.setExecutor(this);
        com.setTabCompleter(this);

        COMMAND_MAP.register(command.getName().toLowerCase(), plugin.getName(), com);

        Permission p1 = new Permission(command.getPermission(), command.getPermissionDefault() == null ? PermissionDefault.OP : command.getPermissionDefault());
        p1.addParent(parentPermission, true);
        PERMISSIONS_LIST.add(p1);
        for (AbstractSubCommand subCommand : command.getSubCommands()) {
            Permission p2 = new Permission(subCommand.getPermission(), subCommand.getPermissionDefault() == null ? PermissionDefault.OP : subCommand.getPermissionDefault());
            p2.addParent(p1, true);
            PERMISSIONS_LIST.add(p2);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        String name = command.getName();
        List<String> args = Arrays.asList(strings);
        for (AbstractCommand<?> com : COMMANDS) {
            if (com.getName().equalsIgnoreCase(name) || (com.getAliases() != null && com.getAliases().contains(name.toLowerCase()))) {
                if (com.hasPermission(sender)) {
                    try {
                        if (args.size() == 1 && args.get(0).equalsIgnoreCase("help")) {
                            com.help(sender, true);
                        } else if (com.canSelfExecute(sender, args)) {
                            com.selfExecute(sender, args);
                        } else if (com.canExecuteSubCommands(sender, args)) {
                            for (AbstractSubCommand subCommand : com.getSubCommands()) {
                                if (subCommand.matching(args)) {
                                    if (subCommand.hasPermission(sender)) {
                                        subCommand.executeCommand(sender, args);
                                    }
                                    return true;
                                }
                            }
                            com.help(sender, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(Languages.FAIL_ERROR);
                    }
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
                if (com.hideCommand()) return null;
                return com.autoComplete(commandSender, Arrays.asList(strings));
            }
        }
        return null;
    }
}
