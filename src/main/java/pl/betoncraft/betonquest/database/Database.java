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
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

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

    protected Plugin plugin;
    protected String prefix;
    protected Connection con;

    protected Database(Plugin plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getConfig().getString("mysql.prefix", "");
    }

    public Connection getConnection() {
        if (con == null) {
            con = openConnection();
        }
        return con;
    }
    
    protected abstract Connection openConnection();

    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con = null;
    }

    public void createTables(boolean isMySQLUsed) {
        String autoIncrement;
        if (isMySQLUsed) {
            autoIncrement = "AUTO_INCREMENT";
        } else {
            autoIncrement = "AUTOINCREMENT";
        }
        // create tables if they don't exist
        Connection connection = getConnection();
        try {
            Debug.info("Creating objectives table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER PRIMARY KEY "
                        + autoIncrement + ", playerID VARCHAR(256) NOT NULL, objective VARCHAR(512)"
                        + " NOT NULL, instructions VARCHAR(2048) NOT NULL);");
            Debug.info("Creating tags table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "tags (id INTEGER PRIMARY KEY " + autoIncrement
                        + ", playerID VARCHAR(256) NOT NULL, tag TEXT NOT NULL);");
            Debug.info("Creating points table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "points (id INTEGER PRIMARY KEY "
                        + autoIncrement + ", playerID "
                        + "VARCHAR(256) NOT NULL, category VARCHAR(256) "
                        + "NOT NULL, count INT NOT NULL);");
            Debug.info("Creating journal table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "journal (id INTEGER PRIMARY KEY "
                        + autoIncrement + ", playerID VARCHAR(256) NOT NULL, pointer "
                        + "VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
            Debug.info("Creating backpack table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "backpack (id INTEGER PRIMARY KEY "
                        + autoIncrement + ", playerID VARCHAR(256) NOT NULL, instruction "
                        + "TEXT NOT NULL, amount INT NOT NULL);");
            Debug.info("Creating player table");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "player (id INTEGER PRIMARY KEY "
                        + autoIncrement + ", playerID VARCHAR(256) NOT NULL, language VARCHAR(16) NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}