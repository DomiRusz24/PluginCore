package me.domirusz24.plugincore.command;

import me.domirusz24.plugincore.command.abstractclasses.BaseSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SchematicLoadCommand extends BaseSubCommand {
    @Override
    protected void execute(CommandSender sender, List<String> args) {

    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>();
    }

    @Override
    protected String name() {
        return "schematicload";
    }

    @Override
    protected String usage() {
        return "schematicload <NAME>";
    }

    @Override
    protected String description() {
        return "Load a schematic.";
    }
}
