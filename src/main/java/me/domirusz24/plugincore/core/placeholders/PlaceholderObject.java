package me.domirusz24.plugincore.core.placeholders;

public interface PlaceholderObject {
    String onPlaceholderRequest(String param);

    String placeHolderPrefix();
}
