package me.domirusz24.plugincore.core.displayable;

import me.domirusz24.plugincore.core.placeholders.PlaceholderObject;
import me.domirusz24.plugincore.managers.PAPIManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringLine {

    private String message;

    private List<PlaceholderObject> objects = new ArrayList<>();

    public StringLine(String message, PlaceholderObject... objects) {
        this.message = message;
        this.objects.addAll(Arrays.asList(objects));
    }

    public List<PlaceholderObject> getObjects() {
        return objects;
    }

    public void setMessage(String string) {
        message = string;
    }

    public String getMessage() {
        String s = message;
        for (PlaceholderObject object : objects) {
            s = PAPIManager.setPlaceholders(object, s);
        }
        return s;
    }
}
