package me.domirusz24.plugincore.core.displayable;


import me.domirusz24.plugincore.CoreListener;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.displayable.interfaces.ClickableItem;
import me.domirusz24.plugincore.core.displayable.interfaces.LeftClickable;
import me.domirusz24.plugincore.core.displayable.interfaces.RightClickable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItemStack implements ClickableItem {

    protected Consumer<Player> leftClick, rightClick;

    protected ItemStack item;

    private final PluginCore plugin;

    public ClickableItemStack(PluginCore plugin, Consumer<Player> leftClick, Consumer<Player> rightClick, ItemStack item) {
        this.plugin = plugin;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.item = item;
        plugin.listener.hookInListener((LeftClickable) this);
        plugin.listener.hookInListener((RightClickable) this);
    }

    public void giveToPlayer(Player player, int amount) {
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        player.getInventory().addItem(clone);
    }

    public void giveToPlayer(Player player, int amount, int slot) {
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        player.getInventory().setItem(slot, clone);
    }

    public void unregister() {
        plugin.listener.removeListener((LeftClickable) this);
        plugin.listener.removeListener((RightClickable) this);
    }

    @Override
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public void onLeftClick(Player player) {
        leftClick.accept(player);
    }

    @Override
    public void onRightClick(Player player) {
        rightClick.accept(player);
    }

    @Override
    public PluginCore getCorePlugin() {
        return plugin;
    }
}
