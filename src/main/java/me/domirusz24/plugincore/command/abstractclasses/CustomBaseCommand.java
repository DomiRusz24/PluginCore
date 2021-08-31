package me.domirusz24.plugincore.command.abstractclasses;

import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CustomBaseCommand<T> extends AbstractCommand<CustomBaseSubCommand<T>> {
    @Override
    public boolean canExecuteSubCommands(CommandSender sender, List<String> args) {
        boolean canExecute = false;
        if (args.size() > 1) {
            String arg = args.get(0);
            T object = translateInstance(arg);
            canExecute = object != null;
            if (!canExecute) {
                onFail(sender, arg);
            }
        }
        return canExecute;
    }

    @Override
    public boolean canSelfExecute(CommandSender sender, List<String> args) {
        return args.size() < 2;
    }

    @Override
    public void selfExecute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            selfExecute(sender);
            return;
        }
        String arg = args.get(0);
        T object = translateInstance(arg);
        if (object != null) {
            selfExecute(sender, object);
        } else {
            selfExecute(sender, arg);
        }
    }

    @Override
    public List<String> autoComplete(CommandSender sender, List<String> args) {
        List<String> complete = new ArrayList<>();
        if (args.size() == 1) {
            complete.addAll(getInstances());
        } else if (args.size() == 2) {
            complete.addAll(getSubCommands().stream().map(Command::getName).collect(Collectors.toList()));
        } else {
            T object = translateInstance(args.get(0));
            if (object != null) {
                for (CustomBaseSubCommand<T> command : getSubCommands()) {
                    if (command.getName().equalsIgnoreCase(args.get(1)) || command.getAliases().contains(args.get(1).toLowerCase())) {
                        return command.autoComplete(sender, object, args.subList(2, args.size()));
                    }
                }
            }
        }
        return UtilMethods.getPossibleCompletions(args, complete);
    }

    public abstract void onFail(CommandSender sender, String arg);

    public abstract Collection<String> getInstances();

    public abstract T translateInstance(String arg);



    public abstract void selfExecute(CommandSender sender, T object);

    public abstract void selfExecute(CommandSender sender, String arg);
}
