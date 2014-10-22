package pl.betoncraft.betonquest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

/**
 * Represents MySQL connection to database
 * @author Husky
 */
public class MySQL {
	private final Plugin plugin;
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;
	private Connection connection;

	public MySQL(Plugin plugin, String hostname, String port, String database,
			String username, String password) {
		this.plugin = plugin;
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		this.connection = null;
	}

	public Connection openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://"
					+ this.hostname + ":" + this.port + "/" + this.database,
					this.user, this.password);
		} catch (SQLException e) {
			this.plugin.getLogger().log(
					Level.SEVERE,
					"Could not connect to MySQL server! because: "
							+ e.getMessage());
		} catch (ClassNotFoundException e) {
			this.plugin.getLogger().log(Level.SEVERE, "JDBC Driver not found!");
		}
		return this.connection;
	}

	public boolean checkConnection() {
		return this.connection != null;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
				this.connection = null;
			} catch (SQLException e) {
				this.plugin.getLogger().log(Level.SEVERE,
						"Error closing the MySQL Connection!");
				e.printStackTrace();
			}
		}
	}

	public ResultSet querySQL(String query) {
		try {
			return this.connection.createStatement().executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updateSQL(String update) {
		try {
			this.connection.createStatement().executeUpdate(update);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}