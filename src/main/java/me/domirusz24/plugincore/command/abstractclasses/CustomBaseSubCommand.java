package me.domirusz24.plugincore.command.abstractclasses;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomBaseSubCommand<T> extends BaseSubCommand {
    @Override
    public boolean matching(List<String> args) {
        if (args.size() > 1) {
            return args.get(1).equalsIgnoreCase(getName()) || getAliases().contains(args.get(1).toLowerCase());
        }
        return false;
    }



    @Override
    protected void execute(CommandSender sender, List<String> args) {
        if (args.size() > 1) {
            String arg = args.get(0);
            T object = translateInstance(arg);
            if (object != null) {
                execute(sender, object , args.subList(2, args.size()));
            } else {
                execute(sender, arg, args.subList(2, args.size()));
            }
        }
    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>();
    }

    @Override
    public void executeCommand(CommandSender sender, List<String> args) {
        execute(sender, args);
    }

    public abstract void execute(CommandSender sender, T object, List<String> args);

    public abstract void execute(CommandSender sender, String arg, List<String> args);

    public abstract List<String> autoComplete(CommandSender sender, T object, List<String> args);

    public abstract T translateInstance(String arg);
}
