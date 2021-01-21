package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.utils.LogUtils;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public abstract class Database {

    protected Plugin plugin;
    protected String prefix;
    protected Connection con;

    protected Database(final Plugin plugin) {
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
            LogUtils.getLogger().log(Level.SEVERE, "There was an exception with SQL");
            LogUtils.logThrowable(e);
        }
        con = null;
    }

    @SuppressFBWarnings({"SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", "OBL_UNSATISFIED_OBLIGATION", "ODR_OPEN_DATABASE_RESOURCE"})
    public void createTables(final boolean isMySQLUsed) {
        final String autoIncrement;
        if (isMySQLUsed) {
            autoIncrement = "AUTO_INCREMENT";
        } else {
            autoIncrement = "AUTOINCREMENT";
        }
        try {
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "objectives (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, objective VARCHAR(512)"
                            + " NOT NULL, instructions VARCHAR(2048) NOT NULL);");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "tags (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, tag TEXT NOT NULL);");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "points (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, category VARCHAR(256) "
                            + "NOT NULL, count INT NOT NULL);");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "journal (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, pointer "
                            + "VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "backpack (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, instruction "
                            + "TEXT NOT NULL, amount INT NOT NULL);");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "player (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", playerID VARCHAR(256) NOT NULL, language VARCHAR(16) NOT NULL, "
                            + "conversation VARCHAR(512));");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_tags (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", tag TEXT NOT NULL);");
            getConnection().createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "global_points (id INTEGER PRIMARY KEY "
                            + autoIncrement + ", category VARCHAR(256) NOT NULL, count INT NOT NULL);");
        } catch (SQLException e) {
            LogUtils.getLogger().log(Level.SEVERE, "There was an exception with SQL");
            LogUtils.logThrowable(e);
        }
    }
}
