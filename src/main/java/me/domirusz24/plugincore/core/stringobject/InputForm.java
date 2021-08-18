package me.domirusz24.plugincore.core.stringobject;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.config.annotations.Language;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.util.CompleteListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public class InputForm extends AbstractPlayer implements CompleteListener {

    private static final HashMap<UUID, InputForm> INPUT_FORMS = new HashMap<>();

    public static InputForm get(UUID uuid) {
        return INPUT_FORMS.get(uuid);
    }

    private final Player player;

    private final Object[] inputs;

    private final StringObject<?>[] args;
    private final String name;
    private final Consumer<Object[]> onComplete;

    private int index = 0;

    private InputForm(PluginCore plugin, Player player, String name, Consumer<Object[]> onComplete, StringObject<?>... args) {
        super(plugin, player);
        this.onComplete = onComplete;
        this.name = name;
        this.player = player;
        this.args = args;
        this.inputs = new Object[args.length];
        INPUT_FORMS.put(player.getUniqueId(), this);
        player.sendMessage(LANG_START.replaceAll("%input%", args[0].getName()).replaceAll("%name%", name));
        registerListener();
    }

    private void end() {
        INPUT_FORMS.remove(player.getUniqueId());
        unregisterListener();
    }

    private void success() {
        INPUT_FORMS.remove(player.getUniqueId());
        unregisterListener();
        onComplete.accept(inputs);
        player.sendMessage(LANG_FINISH.replaceAll("%name%", name));
    }

    @Language("InputForm.Start")
    public static String LANG_START = "&8Utworzono kreator \"&r%name%&r&8\"!||&8Podaj: &l%input%";

    @Language("InputForm.Input")
    public static String LANG_INPUT = "&8%previous%&a&l+||&8&aPodaj: &l%input%";

    @Language("InputForm.InputDefault")
    public static String LANG_INPUT_DEFAULT = "&8%previous%&a&l+||&8Podaj: &a&l%input%&r&8 (\"DEFAULT_VALUE\" dla podstawowej wartości)";

    @Language("InputForm.Fail")
    public static String LANG_FAIL = "&8Nie poprawna forma!||&8Podaj: &l%input%";

    @Language("InputForm.Finish")
    public static String LANG_FINISH = "&8&lUtworzono \"&r%name%&r&8\"!";

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;
        event.setCancelled(true);
        StringObject<?> arg = args[index];
        Object object;
        if ("DEFAULT_VALUE".equals(event.getMessage()) && args[index].getDefaultValue() != null) {
            object = (args[index].getDefaultValue().getDefaultValue(inputs));
        } else {
            object = arg.fromString(event.getMessage());
        }
        if (object != null) {
            inputs[index] = object;
            index++;
            if (index == args.length) {
                success();
            } else {
                if (args[index].getDefaultValue() == null) {
                    player.sendMessage(LANG_INPUT.replaceAll("%input%", args[index].getName()).replaceAll("%previous%", args[index - 1].getName()));
                } else {
                    player.sendMessage(LANG_INPUT_DEFAULT.replaceAll("%input%", args[index].getName()).replaceAll("%previous%", args[index - 1].getName()));
                }
            }
        } else {
            String s = arg.getCustomFail(event.getMessage());
            if (s == null) {
                player.sendMessage(LANG_FAIL.replaceAll("%input%", args[index].getName()));
            } else {
                player.sendMessage(s);
            }
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    protected void onUnregister() {
        end();
    }

    @Override
    public boolean resetInventory() {
        return false;
    }

    public static class Builder {

        private String name;

        private StringObject<?>[] args;
        private Consumer<Object[]> onComplete;

        private PluginCore plugin;

        public Builder setPlugin(PluginCore plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setArgs(StringObject<?>... args) {
            this.args = args;
            return this;
        }

        public Builder setOnComplete(Consumer<Object[]> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public StringObject<?>[] getArgs() {
            return args;
        }

        public Consumer<Object[]> getOnComplete() {
            return onComplete;
        }

        public InputForm create(Player player) {
            return new InputForm(plugin, player, name, getOnComplete(), getArgs());
        }
    }

}
