package me.domirusz24.plugincore.managers.database;

import me.domirusz24.plugincore.managers.database.values.DataBaseValue;
import me.domirusz24.plugincore.managers.database.values.StringValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PlayerDataBaseTable extends DataBaseTable {
    public PlayerDataBaseTable(DataBaseManager manager) {
        super(manager);
    }

    @Override
    public DataBaseValue<?>[] getValues() {
        List<DataBaseValue<?>> list = new ArrayList<>();
        list.add(new StringValue("name", 32));
        list.addAll(Arrays.asList(_getValues()));
        return list.toArray(new DataBaseValue<?>[0]);
    }

    public abstract DataBaseValue<?>[] _getValues();

    @Override
    public String getIndex() {
        return "uuid";
    }

    @Override
    public int getIndexSize() {
        return 36;
    }
}
