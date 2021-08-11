package me.domirusz24.plugincore.core.stringobject;

import me.domirusz24.plugincore.util.UtilMethods;
import org.bukkit.Location;

public class LocationObj extends StringObject<Location> {

    static {
        new LocationObj(null);
    }

    public LocationObj(String name) {
        super(name);
    }

    @Override
    public StringObject<Location> defaultObject() {
        return new LocationObj(null);
    }

    @Override
    public Class<Location> getClazz() {
        return Location.class;
    }

    @Override
    public String toString(Location object) {
        return UtilMethods.locationToString(object, true);
    }

    @Override
    public Location fromString(String string) {
        return UtilMethods.stringToLocation(string);
    }
}
