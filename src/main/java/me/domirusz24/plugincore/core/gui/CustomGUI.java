package me.domirusz24.plugincore.core.gui;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.GUIManager;
import me.domirusz24.plugincore.util.CompleteListener;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class CustomGUI implements CompleteListener {

    private boolean active = true;

    private final String name;

    private Inventory inventory;

    private HashMap<Integer, GUIItem> items = new HashMap<>();

    private ArrayList<UUID> viewers = new ArrayList<>();

    private final ItemStack emptySlot;

    private int size;

    private final PluginCore plugin;

    public CustomGUI(PluginCore plugin, String name, int size) {
        this.plugin = plugin;
        inventory = Bukkit.createInventory(null, size, name);
        this.name = name;
        this.size = size;
        this.emptySlot = emptySlot();
        debug("Start -> " + size);
        registerListener();
    }

    public CustomGUI(PluginCore plugin, String name, InventoryType type) {
        this.plugin = plugin;
        inventory = Bukkit.createInventory(null, type, name);
        this.emptySlot = emptySlot();
        this.name = name;
        this.size = type.getDefaultSize();
        debug("Start -> " + size);
        registerListener();
    }

    public void refresh() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        debug("Refresh");
        debug("Update");
        onUpdate();

        for (Integer slot : items.keySet()) {
            if (items.get(slot).getItem() != null) {
                inventory.setItem(slot, items.get(slot).getItem().clone());
            }
        }
    }

    protected void clearItems() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        debug("Clear");
        items.clear();
        if (emptySlot != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                registerItem(new GUIItem(this, (p) -> {}, (p) -> {}, emptySlot, i));
            }
        } else {
            ItemStack air = new ItemStack(Material.AIR, 1);
            for (int i = 0; i < inventory.getSize(); i++) {
                registerItem(new GUIItem(this, (p) -> {}, (p) -> {}, air, i));
            }
        }
        onClear();
        refresh();
    }

    private boolean invChange = false;

    public void changeSize(int size) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        if (size == this.size) return;
        debug("Change");
        this.size = size;
        Inventory newInventory = Bukkit.createInventory(null, size, name);
        invChange = true;
        for (UUID p : new ArrayList<>(getViewers())) {
            Bukkit.getPlayer(p).openInventory(newInventory);
        }
        invChange = false;
        inventory = newInventory;
        clearItems();
    }

    public boolean addPlayer(Player player) {
        return addPlayer(player, true);
    }
    public boolean addPlayer(Player player, boolean addToGUIs) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        debug("AddPlayer -> " + player.getName() + " | " + addToGUIs);
        if (!viewers.contains(player.getUniqueId())) {
            debug("AddPlayer -> " + player.getName() + " OPEN");
            silent.add(player.getUniqueId());
            viewers.add(player.getUniqueId());
            GUIManager.PlayerGUIData guiData = plugin.guiM.get(player);
            if (guiData.getCurrent() == null) {
                if (addToGUIs) {
                    guiData.addNew(this);
                }
                firstOpen(player);
                player.openInventory(inventory);
            } else {
                CustomGUI old = guiData.getCurrent();
                if (addToGUIs) {
                    guiData.addNew(this);
                }
                old.onPlayerSwitch(player);
                pageOpen(player);
                old.silent.add(player.getUniqueId());
                player.openInventory(inventory);
                old.silent.remove(player.getUniqueId());
                silent.remove(player.getUniqueId());
            }
            return true;
        } else if (silent.contains(player.getUniqueId())) {
            player.openInventory(inventory);
            debug("AddPlayer -> " + player.getName() + " EXISTING");
            silent.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    private void removePlayer(Player player) {
        removePlayer(player, true);
    }

    private void removePlayer(Player player, boolean close) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        if (!silent.contains(player.getUniqueId())) {
            debug("RemovePlayer -> " + player.getName() + " | " + close);
            viewers.remove(player.getUniqueId());
            GUIManager.PlayerGUIData guiData = plugin.guiM.get(player);
            guiData.closeAll();
            if (close) {
                player.closeInventory();
            }
        } else {
            debug("RemovePlayer -> " + player.getName() + " | " + close + " | SILENT");
        }
    }

    public void leave(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        if (silent.contains(player.getUniqueId())) {
            debug("leave -> " + player.getName() + " | SILENT");
            removePlayer(player);
        } else if (viewers.contains(player.getUniqueId())) {
            debug("leave -> " + player.getName());
            player.closeInventory();
        }
    }

    private final HashSet<UUID> silent = new HashSet<>();

    public void silentLeave(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        if (!silent.contains(player.getUniqueId()) && viewers.contains(player.getUniqueId())) {
            debug("silentleave -> " + player.getName());
            silent.add(player.getUniqueId());
            player.closeInventory();
        } else {
            debug("silentleave -> " + player.getName() + " | FAIL");
        }
    }

    public void onPlayerClose(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        debug("onclose -> " + player.getName());
        viewers.remove(player.getUniqueId());
        silent.remove(player.getUniqueId());
        close(player);
    }


    private void onPlayerSwitch(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        debug("onswitch -> " + player.getName());
        viewers.remove(player.getUniqueId());
        changeGUI(player);
    }

    public void registerItem(GUIItem item) {
        debug("registeritem");
        items.put(item.getSlot(), item);
    }

    public ItemStack createItem(Material type, byte data, String name, boolean glow, String... desc) {
        return UtilMethods.createItem(type, data, name, glow, desc);
    }

    public void onLeftClick(Player player, int slot) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        if (items.containsKey(slot)) {
            debug("leftclick -> " + player.getName() + " (" + slot + ")");
            items.get(slot).onLeftClick(player);
        } else {
            debug("leftclick -> " + player.getName() + " | FAIL (" + slot + ")");
        }
    }

    public void onRightClick(Player player, int slot) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        if (items.containsKey(slot)) {
            debug("rightclick -> " + player.getName() + " (" + slot + ")");
            items.get(slot).onRightClick(player);
        } else {
            debug("rightclick -> " + player.getName() + " | FAIL (" + slot + ")");
        }
    }

    public GUIItem getItem(int slot) {
        if (items.containsKey(slot)) {
            return items.get(slot);
        } else {
            if (emptySlot == null) return null;
            GUIItem item = new GUIItem(this, (p) -> {}, (p) -> {}, emptySlot, slot);
            registerItem(item);
            return item;
        }
    }

    protected int getSizeFor(int amount) {
        return (int) (Math.ceil((float) amount / 9f) * 9);
    }

    public void delete() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called sync!");
        }
        debug("DELETE");
        unregisterListener();
        for (UUID viewer : viewers) {
            Bukkit.getPlayer(viewer).closeInventory();
        }
        viewers.clear();
        inventory.clear();
    }

    public ArrayList<UUID> getViewers() {
        return viewers;
    }

    public Collection<GUIItem> getItems() {
        return items.values();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    protected abstract void onUpdate();

    protected abstract void onClear();

    protected abstract void firstOpen(Player player);

    protected abstract void pageOpen(Player player);

    protected abstract void close(Player player);

    protected abstract void changeGUI(Player player);

    protected abstract ItemStack emptySlot();


    // LISTENERS

    @EventHandler()
    public void onItemMove(InventoryDragEvent event) {
        if (!getViewers().contains(event.getWhoClicked().getUniqueId())) return;
        debug("drag -> " + event.getWhoClicked().getName());
        event.setCancelled(true);
    }

    @EventHandler()
    public void onItemClick(InventoryClickEvent event) {
        if (!getViewers().contains(event.getWhoClicked().getUniqueId())) return;
            event.setCancelled(true);
            debug("click -> " + event.getWhoClicked().getName());
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (event.isLeftClick()) onLeftClick((Player) event.getWhoClicked(), event.getSlot());
                if (event.isRightClick()) onRightClick((Player) event.getWhoClicked(), event.getSlot());
            });
    }

    @EventHandler
    public void onInventoryLeave(InventoryCloseEvent event) {
        if (!getViewers().contains(event.getPlayer().getUniqueId())) return;
        if (!invChange && !silent.contains(event.getPlayer().getUniqueId())) {
            debug("leave -> " + event.getPlayer().getName() + " | 1");
            removePlayer((Player) event.getPlayer(), false);
        } else {
            debug("leave -> " + event.getPlayer().getName() + " | SILENT");
        }
    }

    public static final boolean debug = false;

    public void debug(String message) {
        if (debug) {
            System.out.println("(" + this.getTitle() + ") " + message);
        }
    }

}
