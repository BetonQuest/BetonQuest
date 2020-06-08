/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
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

import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 *
 * @author Jakub Sapalski
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
            LogUtils.getLogger().log(Level.SEVERE, "There was a exception with SQL");
            LogUtils.logThrowable(e);
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
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, objective VARCHAR(512)"
                            + " NOT NULL, instructions VARCHAR(2048) NOT NULL);");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "tags (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, tag TEXT NOT NULL);");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "points (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, category VARCHAR(256) "
                            + "NOT NULL, count INT NOT NULL);");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "journal (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, pointer "
                            + "VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "backpack (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, instruction "
                            + "TEXT NOT NULL, amount INT NOT NULL);");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "player (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, language VARCHAR(16) NOT NULL, "
                            + "conversation VARCHAR(512));");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_tags (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", tag TEXT NOT NULL);");
            connection.createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_points (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", category VARCHAR(256) NOT NULL, count INT NOT NULL);");
        } catch (SQLException e) {
            LogUtils.getLogger().log(Level.SEVERE, "There was a exception with SQL");
            LogUtils.logThrowable(e);
        }
    }
}