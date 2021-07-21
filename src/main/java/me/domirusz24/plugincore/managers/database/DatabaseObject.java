package me.domirusz24.plugincore.managers.database;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.database.values.DataBaseValue;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public interface DatabaseObject {

    DataBaseTable getTable();

    String getIndex();

    default void setStringField(String field, String data) {
        getTable().setStringField(getIndex(), field, data);
    }


    default void setIntegerField(String field, int data) {
        getTable().setIntegerField(getIndex(), field, data);
    }

    default void setBooleanField(String field, boolean bool) {
        getTable().setBooleanField(getIndex(), field, bool);
    }

    default void setListField(String field, ArrayList<String> list) {
        getTable().setListField(getIndex(), field, list);
    }

    default void getIndex(Consumer<ResultSet> consumer) {
        getTable().getIndex(getIndex(), consumer);
    }

    default void getHashMapIndex(Consumer<HashMap<String, Object>> consumer) {
        getTable().getHashMapIndex(getIndex(), consumer);
    }

    default void removeIndex() {
        getTable().removeIndex(getIndex());
    }

    default void exists(Consumer<Boolean> exists) {
        getTable().exists(getIndex(), exists);
    }

    default void putDefault() {
        getTable().putDefault(getIndex());
    }

    default void putDefault(Runnable runnable) {
        getTable().putDefault(getIndex(), runnable);
    }

}
