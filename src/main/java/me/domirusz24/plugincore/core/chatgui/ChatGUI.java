package me.domirusz24.plugincore.core.chatgui;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.command.abstractclasses.BaseCommand;
import me.domirusz24.plugincore.config.annotations.Language;
import me.domirusz24.plugincore.core.players.AbstractPlayer;
import me.domirusz24.plugincore.core.protocollib.wrappers.WrapperPlayServerChat;
import me.domirusz24.plugincore.util.CompleteListener;
import me.domirusz24.plugincore.util.UtilMethods;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ChatGUI extends AbstractPlayer implements CompleteListener {

    @Language("Title.X.Name")
    public static String LANG_X_CHAR = "&c&l{&4&l✖&r&c&l}";

    @Language("Title.X.Description")
    public static String LANG_X_DESCRIPTION = "&c&lLeave";

    public static final String LANG_LINE = "§l§m┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅┅";

    private int latestEventIndex = 0;

    private final HashMap<String, Consumer<Player>> COMMAND_TO_CONSUMER = new HashMap<>();

    public List<WrapperPlayServerChat> chatCache = new LinkedList<>();

    private List<BaseComponent> message = new ArrayList<>();

    public ChatGUI(Player player) {
        super(player);
        registerListener();
        PluginCore.chatGuiM.register(this);
    }

    public void addToCache(WrapperPlayServerChat chat) {
        chatCache.add(chat);
    }

    public void reset() {
        message.clear();
        COMMAND_TO_CONSUMER.clear();
        latestEventIndex = 0;
        newLine(20);
    }

    public void update() {
        _update();
        print(player);
    }


    protected abstract void _update();

    @Override
    protected void onUnregister() {
        reset();
        print(player);
        unregisterListener();
        PluginCore.chatGuiM.unregister(this);
        Bukkit.getScheduler().runTaskAsynchronously(PluginCore.plugin, () -> {
            if (!player.isOnline()) return;
            for (WrapperPlayServerChat chat : chatCache) {
                chat.sendPacket(player);
            }
        });
    }

    private void commandExecute(String com) {
        if (COMMAND_TO_CONSUMER.containsKey(com)) {
            COMMAND_TO_CONSUMER.get(com).accept(player);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player)) return;
        event.setCancelled(true);
        onCommand(event.getMessage());
    }

    public void onCommand(String command) {
        commandExecute(command);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getPlayer().getName().equals(player.getName())) return;
        if (resetOnMove()) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                unregister();
            }
        }
    }

    public abstract boolean resetOnMove();

    @Override
    public boolean resetInventory() {
        return false;
    }

    public static final String PASS_THROUGH = "§5§f§6§7§7§1§5§f";

    public ChatGUI newLine() {
        return newLine(1);
    }

    public ChatGUI putX() {
        return addHoverAndClickMessage(LANG_X_CHAR, LANG_X_DESCRIPTION, (p) -> {
            unregister();
        });
    }

    public void line(String prefix) {
        addMessage(UtilMethods.translateColor(prefix) + LANG_LINE);
    }

    public ChatGUI newLine(int lines) {
        message.add(new TextComponent(StringUtils.repeat("\n", lines)));
        return this;
    }

    public ChatGUI tab() {
        return tab(1);
    }

    public ChatGUI tab(int level) {
        message.add(new TextComponent(StringUtils.repeat("   ", level)));
        return this;
    }

    public ChatGUI addMessage(String text) {
        message.add(new TextComponent(text));
        return this;
    }

    public ChatGUI addHoverMessage(String text, String show) {
        TextComponent textComponent = new TextComponent(text);

        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(show)));

        message.add(textComponent);
        return this;
    }

    public ChatGUI addHoverAndClickMessage(String text, String show, Consumer<Player> consumer) {
        TextComponent textComponent = new TextComponent(text);

        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(show)));

        String command = "/ChatGUIEvent" + latestEventIndex;
        latestEventIndex++;

        COMMAND_TO_CONSUMER.put(command, consumer);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        message.add(textComponent);
        return this;
    }

    public ChatGUI addClickableMessage(String text, String click) {
        TextComponent textComponent = new TextComponent(text);

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click));

        message.add(textComponent);
        return this;
    }

    public ChatGUI addClickableMessage(String text, Consumer<Player> consumer) {
        TextComponent textComponent = new TextComponent(text);

        String command = "/ChatGUIEvent" + latestEventIndex;
        latestEventIndex++;

        COMMAND_TO_CONSUMER.put(command, consumer);

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        message.add(textComponent);
        return this;
    }

    public ChatGUI addSuggestingMessage(String text, String suggest) {
        TextComponent textComponent = new TextComponent(text);

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));

        message.add(textComponent);
        return this;
    }

    private void print(Player player) {
        pass(player, message.toArray(new BaseComponent[0]));
    }

    public static void pass(Player player, String message) {
        pass(player, TextComponent.fromLegacyText(message));
    }

    public static void pass(Player player, final BaseComponent... message) {
        final BaseComponent[] components = (BaseComponent[]) ArrayUtils.addAll(new TextComponent[]{new TextComponent(PASS_THROUGH)}, message);
        player.spigot().sendMessage(components);
    }
}
