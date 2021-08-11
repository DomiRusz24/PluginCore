package me.domirusz24.plugincore.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PAPIManager extends PlaceholderExpansion {

    private final PluginCore plugin;

    public PAPIManager(PluginCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return plugin.getName().toLowerCase();
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        return p == null ? onPlaceholderRequest((Player) null, params) : p.isOnline() ? onPlaceholderRequest(p.getPlayer(), params) : onPlaceholderRequest(p.getName(), params);
    }

    protected abstract String onPlaceholderRequest(String name, String params);

    @Override
    public abstract String onPlaceholderRequest(Player player, String params);

    // setting PlaceHolders

    public static String setPlaceholders(PlaceholderObject object, String text) {
        if (text == null) {
            return null;
        } else {
            if (object.placeHolderPrefix().equals(PluginCore.plugin.getName().toLowerCase())) {
                text = object.onPlaceholderRequest(text);
            } else {
                Matcher m = PlaceholderAPI.getPlaceholderPattern().matcher(text);
                while (m.find()) {
                    String format = m.group(1);
                    if (format.startsWith(PluginCore.plugin.getName().toLowerCase())) {
                        int index = format.indexOf("_");
                        if (index > 0 && index < format.length()) {
                            String params = format.substring(index + 1);
                            String value = object.onPlaceholderRequest(params);
                            if (value != null) {
                                text = text.replaceAll(Pattern.quote(m.group()), Matcher.quoteReplacement(value));
                            } else {
                                text = text.replaceAll(Pattern.quote(m.group()), "null");
                            }
                        }
                    }
                }
            }
            return UtilMethods.translateColor(text);
        }
    }
}