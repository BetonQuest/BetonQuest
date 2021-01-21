package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.utils.LogUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Connects to and uses a SQLite database
 */
@SuppressWarnings("PMD.CommentRequired")
public class SQLite extends Database {
    private final String dbLocation;

    /**
     * Creates a new SQLite instance
     *
     * @param plugin     Plugin instance
     * @param dbLocation Location of the Database (Must end in .db)
     */
    public SQLite(final Plugin plugin, final String dbLocation) {
        super(plugin);
        this.dbLocation = dbLocation;
    }

    @Override
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public Connection openConnection() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        final File file = new File(plugin.getDataFolder(), dbLocation);
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtils.getLogger().log(Level.SEVERE, "Unable to create database!");
                LogUtils.logThrowable(e);
            }
        }
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager
                    .getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath().toString() + "/" + dbLocation);
        } catch (ClassNotFoundException | SQLException e) {
            LogUtils.getLogger().log(Level.SEVERE, "There was an exception with SQL");
            LogUtils.logThrowable(e);
        }
        return connection;
    }
}
