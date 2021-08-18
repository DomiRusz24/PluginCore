package me.domirusz24.plugincore.managers.database;

import me.domirusz24.plugincore.PluginCore;
import me.domirusz24.plugincore.managers.database.values.DataBaseValue;
import javafx.util.Pair;
import org.bukkit.Bukkit;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Consumer;

public abstract class DataBaseTable {

    protected final DataBaseManager manager;

    protected final String index;

    private final DataBaseValue<?>[] values;

    public DataBaseTable(DataBaseManager manager) {
        this.manager = manager;
        this.index = getIndex();
        this.values = getValues();
    }

    public String initTable() {
        StringBuilder path = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + manager.getTablePrefix() + getName() + "` (`" + index + "` varchar(" + getIndexSize() + ") NOT NULL UNIQUE");
        for (DataBaseValue<?> value : values) {
            path.append(", `").append(value.getName()).append("` ").append(value.getValue());
        }
        path.append(");");
        return path.toString();
    }

    public abstract String getName();

    public abstract DataBaseValue<?>[] getValues();

    public abstract String getIndex();

    public int getIndexSize() {
        return 100;
    }

    public void setStringField(String index, final String field, final String data) {
        manager.sqlQueue.add(() -> {
            try {

                PreparedStatement sql1 = manager.connection.prepareStatement("UPDATE `" + manager.getTablePrefix() + getName() + "` SET `" + field + "`=? WHERE `" + this.index + "`=?;");

                sql1.setString(1, data);

                sql1.setString(2, index);

                sql1.executeUpdate();

                sql1.close();

            } catch (Exception e) {

                e.printStackTrace();

            }
        });
    }


    public void setIntegerField(String index, String field, final int data) {
        manager.sqlQueue.add(() -> {
            try {

                PreparedStatement sql1 = manager.connection.prepareStatement("UPDATE `" + manager.getTablePrefix() + getName() + "` SET `" + field + "`=? WHERE `" + this.index + "`=?;");

                sql1.setInt(1, data);

                sql1.setString(2, index);

                sql1.executeUpdate();

                sql1.close();

            } catch (Exception e) {

                e.printStackTrace();

            }
        });
    }

    public void setBooleanField(String index, String field, boolean bool) {
        manager.sqlQueue.add(() -> {
            try {

                PreparedStatement sql1 = manager.connection.prepareStatement("UPDATE `" + manager.getTablePrefix() + getName() + "` SET `" + field + "`=? WHERE `" + this.index + "`=?;");

                sql1.setInt(1, bool ? 1 : 0);

                sql1.setString(2, index);

                sql1.executeUpdate();

                sql1.close();

            } catch (Exception e) {

                e.printStackTrace();

            }
        });
    }

    public void setListField(String index, String field, ArrayList<String> list) {
        manager.sqlQueue.add(() -> {
            try {

                PreparedStatement sql1 = manager.connection.prepareStatement("UPDATE `" + manager.getTablePrefix() + getName() + "` SET `" + field + "`=? WHERE `" + this.index + "`=?;");

                sql1.setString(1, listToString(list));

                sql1.setString(2, index);

                sql1.executeUpdate();

                sql1.close();

            } catch (Exception e) {

                e.printStackTrace();

            }
        });
    }

    public void getIndex(String index, Consumer<ResultSet> consumer) {
        manager.sqlQueue.add(() -> {
            try {
                PreparedStatement sql = manager.connection.prepareStatement("SELECT * FROM `" + manager.getTablePrefix() + getName() + "` WHERE `" + this.index + "`=?;");

                sql.setString(1, index);

                ResultSet rs = sql.executeQuery();

                if (rs.next()) {
                    consumer.accept(rs);
                } else {
                    consumer.accept(null);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getHashMapIndex(String index, Consumer<HashMap<String, Object>> consumer) {
        manager.sqlQueue.add(() -> {
            try {
                PreparedStatement sql = manager.connection.prepareStatement("SELECT * FROM `" + manager.getTablePrefix() + getName() + "` WHERE `" + this.index + "`=?;");

                sql.setString(1, index);

                ResultSet rs = sql.executeQuery();

                if (rs.next()) {
                    HashMap<String, Object> values = new HashMap<>();
                    for (DataBaseValue<?> value : getValues()) {
                        values.put(value.getName(), rs.getObject(value.getName()));
                    }
                    consumer.accept(values);
                } else {
                    consumer.accept(null);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getObjects(Consumer<HashMap<String, HashMap<String, Object>>> consumer) {
        manager.sqlQueue.add(() -> {
            try {
                PreparedStatement sql = manager.connection.prepareStatement("SELECT * FROM `" + manager.getTablePrefix() + getName() + "`");

                ResultSet rs = sql.executeQuery();

                HashMap<String, HashMap<String, Object>> indexes = new HashMap<>();

                while (rs.next()) {
                    String id = rs.getString(getIndex());
                    indexes.put(id, new HashMap<>());
                    for (DataBaseValue<?> value : getValues()) {
                        indexes.get(id).put(value.getName(), rs.getObject(value.getName()));
                    }
                }

                consumer.accept(indexes);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getIndexes(Consumer<List<String>> consumer) {
        manager.sqlQueue.add(() -> {
            try {
                PreparedStatement sql = manager.connection.prepareStatement("SELECT * FROM `" + manager.getTablePrefix() + getName() + "`");

                ResultSet rs = sql.executeQuery();

                List<String> indexes = new ArrayList<>();

                while (rs.next()) {
                    indexes.add(rs.getString(getIndex()));
                }

                consumer.accept(indexes);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void removeIndex(String index) {
        manager.sqlQueue.add(() -> {
            try {
                PreparedStatement sql = manager.connection.prepareStatement("DELETE FROM `" + manager.getTablePrefix() + getName() + "` WHERE `" + this.index + "`=?;");

                sql.setString(1, index);

                sql.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void exists(String index, Consumer<Boolean> exists) {
        getIndex(index, (rs) -> {
            try {
                if (rs != null) {
                    Bukkit.getScheduler().runTask(manager.getPlugin(), () -> exists.accept(true));
                } else {
                    Bukkit.getScheduler().runTask(manager.getPlugin(), () -> exists.accept(false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void putDefault(String index) {
        putDefault(index, () -> {});
    }

    public void putDefault(String index, Runnable runnable) {
        manager.sqlQueue.add(() -> {
            try {
                StringBuilder pathString;
                if (manager.isSqlLite()) {
                    pathString = new StringBuilder("INSERT or IGNORE INTO `" + manager.getTablePrefix() + getName() + "` (`" + this.index + "`");
                } else {
                    pathString = new StringBuilder("INSERT IGNORE INTO `" + manager.getTablePrefix() + getName() + "` (`" + this.index + "`");
                }
                StringBuilder valuesString = new StringBuilder(") VALUES('" + index + "'");
                for (DataBaseValue<?> value : values) {
                    pathString.append(", `").append(value.getName()).append("`");
                    valuesString.append(", '").append(value.getDefaultValue()).append("'");
                }

                pathString.append(valuesString).append(");");

                PreparedStatement statement = manager.connection.prepareStatement(pathString.toString());

                statement.executeUpdate();
                runnable.run();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(';');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String listToString(String[] list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(';');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static ArrayList<String> stringToList(String string) {
        ArrayList<String> s = new ArrayList<>();
        Collections.addAll(s, string.split(";"));
        return s;
    }

}
