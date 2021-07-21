package me.domirusz24.plugincore.command;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.abstractclasses.BaseSubCommand;
import me.domirusz24.plugincore.util.Pair;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SchematicSaveCommand extends BaseSubCommand {
    @Override
    protected void execute(CommandSender sender, List<String> args) {
        if (isPlayer(sender) && correctLength(sender, args.size(), 1, 1)) {
            Pair<Location, Location> selection = PluginCore.worldEditM.getPlayerSelection((Player) sender);
            if (selection == null) {
                sender.sendMessage(Languages.INCORRECT_SELECTION);
            } else {
                PluginCore.worldEditM.saveSchematic(sender, selection.getKey(), selection.getValue(), args.get(0));
                sender.sendMessage(Languages.SUCCESS);
            }
        }
    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>();
    }

    @Override
    protected String name() {
        return "saveschematic";
    }

    @Override
    protected String usage() {
        return "saveschematic <NAME>";
    }

    @Override
    protected String description() {
        return "Saves a schematic.";
    }
}
