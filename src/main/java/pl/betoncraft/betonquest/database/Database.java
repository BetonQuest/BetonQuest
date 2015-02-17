/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 * 
 * @author -_Husky_-
 * @author tips48
 * @author Co0sh
 */
public abstract class Database {

    protected Connection connection;
    protected Plugin plugin;
    protected String prefix;

    protected Database(Plugin plugin) {
        this.plugin = plugin;
        this.connection = null;
        this.prefix = plugin.getConfig().getString("mysql.prefix", "");
    }

    public abstract Connection openConnection();

    public void closeConnection() {
        if (BetonQuest.getInstance().isMySQLUsed())
            return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection = null;
    }

    public ResultSet querySQL(QueryType type, String[] args) {
        try {
            PreparedStatement statement;
            switch (type) {
                case SELECT_JOURNAL:
                    statement = connection
                            .prepareStatement("SELECT pointer, date FROM " + prefix + "journal WHERE playerID = ?;");
                    break;
                case SELECT_POINTS:
                    statement = connection
                            .prepareStatement("SELECT category, count FROM " + prefix + "points WHERE playerID = ?;");
                    break;
                case SELECT_UNUSED_OBJECTIVES:
                    statement = connection
                            .prepareStatement("SELECT instructions FROM " + prefix + "objectives WHERE playerID = ? AND isused= 0;");
                    break;
                case SELECT_UNUSED_TAGS:
                    statement = connection
                            .prepareStatement("SELECT tag FROM " + prefix + "tags WHERE playerID = ? AND isused = 0;");
                    break;
                case SELECT_USED_OBJECTIVES:
                    statement = connection
                            .prepareStatement("SELECT instructions FROM " + prefix + "objectives WHERE playerID = ? AND isused= 1;");
                    break;
                case SELECT_USED_TAGS:
                    statement = connection
                            .prepareStatement("SELECT tag FROM " + prefix + "tags WHERE playerID = ? AND isused = 1;");
                    break;
                case SELECT_PLAYERS_TAGS:
                    statement = connection
                            .prepareStatement("SELECT playerID FROM " + prefix + "tags GROUP BY playerID;");
                    break;
                case SELECT_PLAYERS_JOURNAL:
                    statement = connection
                            .prepareStatement("SELECT playerID FROM " + prefix + "journal GROUP BY playerID;");
                    break;
                case SELECT_PLAYERS_POINTS:
                    statement = connection
                            .prepareStatement("SELECT playerID FROM " + prefix + "points GROUP BY playerID;");
                    break;
                case SELECT_PLAYERS_OBJECTIVES:
                    statement = connection
                            .prepareStatement("SELECT playerID FROM " + prefix + "objectives GROUP BY playerID;");
                    break;
                default:
                    statement = null;
                    break;
            }
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateSQL(UpdateType type, String[] args) {
        try {
            PreparedStatement statement;
            switch (type) {
                case DELETE_USED_OBJECTIVES:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "objectives WHERE playerID = ? AND isused = 1;");
                    break;
                case DELETE_POINTS:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "points WHERE playerID = ?;");
                    break;
                case ADD_NEW_OBJECTIVE:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "objectives (playerID, instructions, isused) VALUES (?, ?, 0);");
                    break;
                case ADD_POINTS:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "points (playerID, category, count) VALUES (?, ?, ?);");
                    break;
                case DELETE_TAGS:
                    statement = connection.prepareStatement("DELETE FROM " + prefix + "tags WHERE playerID = ?;");
                    break;
                case ADD_TAGS:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "tags (playerID, tag) VALUES (?, ?);");
                    break;
                case DELETE_JOURNAL:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "journal WHERE playerID = ?;");
                    break;
                case ADD_JOURNAL:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "journal (playerID, pointer, date) VALUES (?, ?, ?);");
                    break;
                case DELETE_ALL_OBJECTIVES:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "objectives WHERE playerID = ?;");
                    break;
                case UPDATE_OBJECTIVES:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "objectives SET isused = 1 WHERE playerID = ? AND isused = 0;");
                    break;
                case UPDATE_TAGS:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "tags SET isused = 1 WHERE playerID = ? AND isused = 0;");
                    break;
                case UPDATE_PLAYERS_TAGS:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "tags SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_JOURNAL:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "journal SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_POINTS:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "points SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_OBJECTIVES:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "objectives SET playerID = ? WHERE playerID = ?;");
                    break;
                default:
                    statement = null;
                    break;
            }
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables(boolean isMySQLUsed) {
        String autoIncrement;
        if (isMySQLUsed) {
            autoIncrement = "AUTO_INCREMENT";
        } else {
            autoIncrement = "AUTOINCREMENT";
        }
        // create tables if they don't exist
        Connection connection = openConnection();
        try {
            Debug.info("Creating objectives table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER" + " PRIMARY KEY "
                        + autoIncrement + ", playerID " + "VARCHAR(256) NOT NULL, instructions "
                        + "VARCHAR(2048) NOT NULL, isused BOOLEAN NOT NULL" + " DEFAULT 0);");
            Debug.info("Creating tags table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "tags (id INTEGER " + "PRIMARY KEY " + autoIncrement
                        + ", playerID " + "VARCHAR(256) NOT NULL, tag TEXT NOT NULL, "
                        + "isused BOOLEAN NOT NULL DEFAULT 0);");
            Debug.info("Creating points table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "points (id INTEGER " + "PRIMARY KEY "
                        + autoIncrement + ", playerID "
                        + "VARCHAR(256) NOT NULL, category VARCHAR(256) "
                        + "NOT NULL, count INT NOT NULL);");
            Debug.info("Creating journal table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "journal (id " + "INTEGER PRIMARY KEY "
                        + autoIncrement + ", playerID VARCHAR(256) NOT NULL, pointer "
                        + "VARCHAR(256) NOT NULL, date TIMESTAMP NOT " + "NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public enum QueryType {

        SELECT_USED_OBJECTIVES, SELECT_UNUSED_OBJECTIVES, SELECT_USED_TAGS, SELECT_UNUSED_TAGS,
        SELECT_POINTS, SELECT_JOURNAL,

        SELECT_PLAYERS_TAGS, SELECT_PLAYERS_JOURNAL, SELECT_PLAYERS_POINTS,
        SELECT_PLAYERS_OBJECTIVES,

    }

    public enum UpdateType {

        ADD_NEW_OBJECTIVE, DELETE_USED_OBJECTIVES, DELETE_POINTS, ADD_POINTS, DELETE_TAGS,
        ADD_TAGS, DELETE_JOURNAL, ADD_JOURNAL,

        DELETE_ALL_OBJECTIVES,

        UPDATE_OBJECTIVES, UPDATE_TAGS,

        UPDATE_PLAYERS_TAGS, UPDATE_PLAYERS_JOURNAL, UPDATE_PLAYERS_POINTS,
        UPDATE_PLAYERS_OBJECTIVES,
    }
}