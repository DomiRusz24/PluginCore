package me.domirusz24.plugincore.core.gui.shortcut;

import me.domirusz24.plugincore.core.gui.GUIItem;
import org.bukkit.inventory.ItemStack;

public class EmptyItem extends GUIItem {
    public EmptyItem(ItemStack item, int slot) {
        super(
                (t) -> {},
                (t) -> {},
                item,
                slot);
    }
}
