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

import pl.betoncraft.betonquest.BetonQuest;

/**
 * Connects to the database and queries it
 * 
 * @author Coosh
 */
public class Connector {
    
    private BetonQuest plugin;
    private String prefix;
    private Database db;
    private Connection connection;

    /**
     * Opens a new connection to the database
     */
    public Connector() {
        plugin = BetonQuest.getInstance();
        prefix = plugin.getConfig().getString("mysql.prefix", "");
        db = plugin.getDB();
        connection = db.openConnection();
    }
    
    /**
     * Queries the database with the given type and arguments
     * 
     * @param type
     *          type of the query
     * @param args
     *          arguments
     * @return ResultSet with the requsted data
     */
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
                case SELECT_OBJECTIVES:
                    statement = connection
                            .prepareStatement("SELECT instructions FROM " + prefix + "objectives WHERE playerID = ?;");
                    break;
                case SELECT_TAGS:
                    statement = connection
                            .prepareStatement("SELECT tag FROM " + prefix + "tags WHERE playerID = ?;");
                    break;
                case SELECT_BACKPACK:
                    statement = connection
                            .prepareStatement("SELECT instruction, amount FROM " + prefix + "backpack WHERE playerID = ?;");
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
                case SELECT_PLAYERS_BACKPACK:
                    statement = connection
                            .prepareStatement("SELECT playerID FROM " + prefix + "backpack GROUP BY playerID;");
                    break;
                case LOAD_ALL_JOURNALS:
                    statement = connection.prepareStatement("SELECT * FROM " + prefix + "journal");
                    break;
                case LOAD_ALL_OBJECTIVES:
                    statement = connection.prepareStatement("SELECT * FROM " + prefix + "objectives");
                    break;
                case LOAD_ALL_POINTS:
                    statement = connection.prepareStatement("SELECT * FROM " + prefix + "points");
                    break;
                case LOAD_ALL_TAGS:
                    statement = connection.prepareStatement("SELECT * FROM " + prefix + "tags");
                    break;
                case LOAD_ALL_BACKPACK:
                    statement = connection.prepareStatement("SELECT * FROM " + prefix + "backpack");
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

    /**
     * Updates the database with the given type and arguments
     * 
     * @param type
     *          type of the update
     * @param args
     *          arguments
     */
    public void updateSQL(UpdateType type, String[] args) {
        try {
            PreparedStatement statement;
            switch (type) {
                case ADD_OBJECTIVES:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "objectives (playerID, instructions) VALUES (?, ?);");
                    break;
                case ADD_TAGS:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "tags (playerID, tag) VALUES (?, ?);");
                    break;
                case ADD_POINTS:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "points (playerID, category, count) VALUES (?, ?, ?);");
                    break;
                case ADD_JOURNAL:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "journal (playerID, pointer, date) VALUES (?, ?, ?);");
                    break;
                case ADD_BACKPACK:
                    statement = connection
                            .prepareStatement("INSERT INTO " + prefix + "backpack (playerID, instruction, amount) VALUES (?, ?, ?);");
                    break;
                case DELETE_OBJECTIVES:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "objectives WHERE playerID = ?;");
                    break;
                case DELETE_TAGS:
                    statement = connection.prepareStatement("DELETE FROM " + prefix + "tags WHERE playerID = ?;");
                    break;
                case DELETE_POINTS:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "points WHERE playerID = ?;");
                    break;
                case DELETE_JOURNAL:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "journal WHERE playerID = ?;");
                    break;
                case DELETE_BACKPACK:
                    statement = connection
                            .prepareStatement("DELETE FROM " + prefix + "backpack WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_OBJECTIVES:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "objectives SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_TAGS:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "tags SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_POINTS:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "points SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_JOURNAL:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "journal SET playerID = ? WHERE playerID = ?;");
                    break;
                case UPDATE_PLAYERS_BACKPACK:
                    statement = connection
                            .prepareStatement("UPDATE " + prefix + "backpack SET playerID = ? WHERE playerID = ?;");
                    break;
                case DROP_OBJECTIVES:
                    statement = connection.prepareStatement("DROP TABLE " + prefix + "objectives");
                    break;
                case DROP_TAGS:
                    statement = connection.prepareStatement("DROP TABLE " + prefix + "tags");
                    break;
                case DROP_POINTS:
                    statement = connection.prepareStatement("DROP TABLE " + prefix + "points");
                    break;
                case DROP_JOURNALS:
                    statement = connection.prepareStatement("DROP TABLE " + prefix + "journal");
                    break;
                case DROP_BACKPACK:
                    statement = connection.prepareStatement("DROP TABLE " + prefix + "backpack");
                    break;
                case INSERT_OBJECTIVE:
                    statement = connection.prepareStatement("INSERT INTO " + prefix + "objectives "
                        + "VALUES (?,?,?)");
                    break;
                case INSERT_TAG:
                    statement = connection.prepareStatement("INSERT INTO " + prefix + "tags "
                        + "VALUES (?,?,?)");
                    break;
                case INSERT_POINT:
                    statement = connection.prepareStatement("INSERT INTO " + prefix + "points "
                        + "VALUES (?,?,?,?)");
                    break;
                case INSERT_JOURNAL:
                    statement = connection.prepareStatement("INSERT INTO " + prefix + "journal "
                        + "VALUES (?,?,?,?)");
                    break;
                case INSERT_BACKPACK:
                    statement = connection.prepareStatement("INSERT INTO " + prefix + "backpack "
                        + "VALUES (?,?,?,?)");
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
    
    /**
     * Closes the connection, finishing this object's job 
     */
    public void close() {
        db.closeConnection(connection);
    }

    /**
     * Type of the query
     */
    public enum QueryType {

        SELECT_OBJECTIVES, SELECT_TAGS, SELECT_POINTS, SELECT_JOURNAL, SELECT_BACKPACK,

        SELECT_PLAYERS_TAGS, SELECT_PLAYERS_JOURNAL, SELECT_PLAYERS_POINTS,
        SELECT_PLAYERS_OBJECTIVES, SELECT_PLAYERS_BACKPACK,
        
        LOAD_ALL_OBJECTIVES, LOAD_ALL_TAGS, LOAD_ALL_POINTS, LOAD_ALL_JOURNALS,
        LOAD_ALL_BACKPACK

    }

    /**
     * Type of the update
     */
    public enum UpdateType {

        ADD_OBJECTIVES, ADD_TAGS, ADD_POINTS, ADD_JOURNAL, ADD_BACKPACK,
        DELETE_OBJECTIVES, DELETE_TAGS, DELETE_POINTS, DELETE_JOURNAL, DELETE_BACKPACK,

        UPDATE_PLAYERS_OBJECTIVES, UPDATE_PLAYERS_TAGS, UPDATE_PLAYERS_POINTS,
        UPDATE_PLAYERS_JOURNAL, UPDATE_PLAYERS_BACKPACK,
        
        DROP_OBJECTIVES, DROP_TAGS, DROP_POINTS, DROP_JOURNALS, DROP_BACKPACK,
        INSERT_OBJECTIVE, INSERT_TAG, INSERT_POINT, INSERT_JOURNAL, INSERT_BACKPACK
    }

}
