package me.domirusz24.plugincore.core.displayable;



import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.managers.PAPIManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;

public abstract class Displayable {

    public final PluginCore plugin;

    public Displayable(PluginCore pluginCore, PlaceholderObject... placeholders) {
        this.placeholders.addAll(Arrays.asList(placeholders));
        this.plugin = pluginCore;
    }

    private int size = 100;

    private final List<PlaceholderObject> placeholders = new ArrayList<>();

    private final TreeMap<Integer, Supplier<String>> values = new TreeMap<>();

    public void addPlaceholder(PlaceholderObject object) {
        placeholders.add(object);
    }

    public void removePlaceholder(PlaceholderObject object) {
        placeholders.remove(object);
    }

    private final ArrayList<String> tempValues = new ArrayList<>();

    public void update() {
        tempValues.clear();
        for (int i = 0; i < getLastSlot() + 1; i++) {
            tempValues.add(new String(new char[i + 1]).replace("\0", " "));
        }
        for (Integer i : values.keySet()) {
            String s = values.get(i).get();
            for (PlaceholderObject object : placeholders) {
                s = PAPIManager.setPlaceholders(plugin, object, s);
            }
            tempValues.set(i, s);
        }
        onUpdate(tempValues);
    }

    public void setSize(int size) {
        this.size = size;
        for (Integer slot : values.keySet()) {
            if (slot >= size) {
                values.remove(slot);
            }
        }
    }

    public int getSize() {
        return size;
    }

    protected abstract void onUpdate(ArrayList<String> values);

    private int getLastSlot() {
        int largest = -1;
        for (Integer i : values.keySet()) {
            largest = Math.max(largest, i);
        }
        return largest;
    }

    public Displayable addValue(String... string) {
        int slot = getLastSlot();
        for (String s : string) {
            slot++;
            addValue(slot, () -> s);
        }
        return this;
    }

    public Displayable addValue(StringLine... string) {
        int slot = getLastSlot();
        for (StringLine s : string) {
            slot++;
            addValue(slot, s::getMessage);
        }
        return this;
    }

    public void addValue(Supplier<String> string) {
        addValue(getLastSlot() + 1, string);
    }

    public void addValue(int slot, Supplier<String> string) {
        if (slot < size) {
            values.put(slot, string);
        }
    }

    public Displayable addValue(int slot, StringLine string) {
        addValue(slot, string::getMessage);
        return this;
    }

    public Displayable addValue(int slot, String string) {
        addValue(slot, () -> string);
        return this;
    }

    public void resetValues() {
        values.clear();
    }

    public List<PlaceholderObject> getPlaceholders() {
        return placeholders;
    }



}
