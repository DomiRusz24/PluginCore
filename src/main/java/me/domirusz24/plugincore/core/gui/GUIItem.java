package me.domirusz24.plugincore.core.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class GUIItem {

    protected Consumer<Player> leftClick, rightClick;

    protected ItemStack item;

    protected int slot;

    private final CustomGUI gui;

    public GUIItem(CustomGUI gui, Consumer<Player> leftClick, Consumer<Player> rightClick, ItemStack item, int slot) {
        this.gui = gui;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.item = item.clone();
        this.slot = slot;
    }

    public GUIItem setLeftClick(Consumer<Player> leftClick) {
        this.leftClick = leftClick;
        return this;
    }

    public GUIItem setRightClick(Consumer<Player> rightClick) {
        this.rightClick = rightClick;
        return this;
    }

    public GUIItem setClick(Consumer<Player> click) {
        setLeftClick(click);
        setRightClick(click);
        return this;
    }

    public GUIItem clearLeftClick(Consumer<Player> leftClick) {
        return setLeftClick((p) -> {});
    }

    public GUIItem clearRightClick(Consumer<Player> rightClick) {
        return setRightClick((p) -> {});
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public GUIItem setItem(ItemStack item) {
        this.item = item.clone();
        return this;
    }

    public GUIItem makeEmpty() {
        setName("");
        setDescription("");
        setAmount(1);
        return this;
    }

    public GUIItem makeGoBackwards() {
        setLeftClick((p) -> {
            gui.getCorePlugin().guiM.get(p).goBackwards();
        });
        return this;
    }

    public GUIItem makeGoForwards() {
        setLeftClick((p) -> {
            gui.getCorePlugin().guiM.get(p).goForward();
        });
        return this;
    }

    public GUIItem setName(String name) {
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(name);
        item.setItemMeta(m);
        return this;
    }

    public GUIItem setDescription(String... lines) {
        ItemMeta m = item.getItemMeta();
        m.setLore(Arrays.asList(lines));
        item.setItemMeta(m);
        return this;
    }

    public GUIItem setMaterial(Material material) {
        item.setType(material);
        return this;
    }

    public GUIItem setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public GUIItem setGlow(boolean glow) {
        if (glow) {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addEnchant(Enchantment.LUCK, 1, true);
            item.setItemMeta(meta);
        } else {
            item.removeEnchantment(Enchantment.LUCK);
        }
        return this;
    }

    public void toggleGlowing() {
        setGlow(!isGlowing());
    }

    public boolean isGlowing() {
        return item.getEnchantments().containsKey(Enchantment.LUCK);
    }

    public boolean isTheSame(ItemStack compare) {
        if (compare.getType() == item.getType()) {
            if (compare.getItemMeta() != null && item.getItemMeta() != null) {
                return Objects.equals(compare.getItemMeta().getDisplayName(), item.getItemMeta().getDisplayName());
            } else {
                return compare.getItemMeta() == null && item.getItemMeta() == null;
            }
        }
        return false;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void onLeftClick(Player player) {
        leftClick.accept(player);
    }

    public void onRightClick(Player player) {
        rightClick.accept(player);
    }


}
