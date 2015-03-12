package pl.betoncraft.betonquest.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 * 
 * @author -_Husky_-
 * @author tips48
 */
public abstract class Database {

	protected Connection connection;
	protected Plugin plugin;
	
	protected Database(Plugin plugin) {
		this.plugin = plugin;
		this.connection = null;
	}
	
	public abstract Connection openConnection();
	
	public void closeConnection() {
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
				statement = connection.prepareStatement("SELECT pointer, date FROM journal WHERE playerID = ?;");
				break;
			case SELECT_POINTS:
				statement = connection.prepareStatement("SELECT category, count FROM points WHERE playerID = ?;");
				break;
			case SELECT_UNUSED_OBJECTIVES:
				statement = connection.prepareStatement("SELECT instructions FROM objectives WHERE playerID = ? AND isused= 0;");
				break;
			case SELECT_UNUSED_TAGS:
				statement = connection.prepareStatement("SELECT tag FROM tags WHERE playerID = ? AND isused = 0;");
				break;
			case SELECT_USED_OBJECTIVES:
				statement = connection.prepareStatement("SELECT instructions FROM objectives WHERE playerID = ? AND isused= 1;");
				break;
			case SELECT_USED_TAGS:
				statement = connection.prepareStatement("SELECT tag FROM tags WHERE playerID = ? AND isused = 1;");
				break;
			case SELECT_PLAYERS_TAGS:
				statement = connection.prepareStatement("SELECT playerID FROM tags GROUP BY playerID;");
				break;
			case SELECT_PLAYERS_JOURNAL:
				statement = connection.prepareStatement("SELECT playerID FROM journal GROUP BY playerID;");
				break;
			case SELECT_PLAYERS_POINTS:
				statement = connection.prepareStatement("SELECT playerID FROM points GROUP BY playerID;");
				break;
			case SELECT_PLAYERS_OBJECTIVES:
				statement = connection.prepareStatement("SELECT playerID FROM objectives GROUP BY playerID;");
				break;
			default:
				statement = null;
				break;
			}
			for (int i = 0; i < args.length; i++) {
				statement.setString(i+1, args[i]);
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
				statement = connection.prepareStatement("DELETE FROM objectives WHERE playerID = ? AND isused = 1;");
				break;
			case DELETE_POINTS:
				statement = connection.prepareStatement("DELETE FROM points WHERE playerID = ?;");
				break;
			case ADD_NEW_OBJECTIVE:
				statement = connection.prepareStatement("INSERT INTO objectives (playerID, instructions, isused) VALUES (?, ?, 0);");
				break;
			case ADD_POINTS:
				statement = connection.prepareStatement("INSERT INTO points (playerID, category, count) VALUES (?, ?, ?);");
				break;
			case DELETE_TAGS:
				statement = connection.prepareStatement("DELETE FROM tags WHERE playerID = ?;");
				break;
			case ADD_TAGS:
				statement = connection.prepareStatement("INSERT INTO tags (playerID, tag) VALUES (?, ?);");
				break;
			case DELETE_JOURNAL:
				statement = connection.prepareStatement("DELETE FROM journal WHERE playerID = ?;");
				break;
			case ADD_JOURNAL:
				statement = connection.prepareStatement("INSERT INTO journal (playerID, pointer, date) VALUES (?, ?, ?);");
				break;
			case DELETE_ALL_OBJECTIVES:
				statement = connection.prepareStatement("DELETE FROM objectives WHERE playerID = ?;");
				break;
			case UPDATE_OBJECTIVES:
				statement = connection.prepareStatement("UPDATE objectives SET isused = 1 WHERE playerID = ? AND isused = 0;");
				break;
			case UPDATE_TAGS:
				statement = connection.prepareStatement("UPDATE tags SET isused = 1 WHERE playerID = ? AND isused = 0;");
				break;
			case UPDATE_PLAYERS_TAGS:
				statement = connection.prepareStatement("UPDATE tags SET playerID = ? WHERE playerID = ?;");
				break;
			case UPDATE_PLAYERS_JOURNAL:
				statement = connection.prepareStatement("UPDATE journal SET playerID = ? WHERE playerID = ?;");
				break;
			case UPDATE_PLAYERS_POINTS:
				statement = connection.prepareStatement("UPDATE points SET playerID = ? WHERE playerID = ?;");
				break;
			case UPDATE_PLAYERS_OBJECTIVES:
				statement = connection.prepareStatement("UPDATE objectives SET playerID = ? WHERE playerID = ?;");
				break;
	                case DROP_OBJECTIVES:
	                    statement = connection.prepareStatement("DROP TABLE objectives");
	                    break;
	                case DROP_TAGS:
	                    statement = connection.prepareStatement("DROP TABLE tags");
	                    break;
	                case DROP_POINTS:
	                    statement = connection.prepareStatement("DROP TABLE points");
	                    break;
	                case DROP_JOURNALS:
	                    statement = connection.prepareStatement("DROP TABLE journal");
	                    break;
	                case INSERT_OBJECTIVE:
	                    statement = connection.prepareStatement("INSERT INTO objectives "
	                        + "VALUES (?,?,?,?)");
	                    break;
	                case INSERT_TAG:
	                    statement = connection.prepareStatement("INSERT INTO tags "
	                        + "VALUES (?,?,?,?)");
	                    break;
	                case INSERT_POINT:
	                    statement = connection.prepareStatement("INSERT INTO points "
	                        + "VALUES (?,?,?,?)");
	                    break;
	                case INSERT_JOURNAL:
	                    statement = connection.prepareStatement("INSERT INTO journal "
	                        + "VALUES (?,?,?,?)");
	                    break;
			default:
				statement = null;
				break;
			}
			for (int i = 0; i < args.length; i++) {
				statement.setString(i+1, args[i]);
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
                    connection
                                    .createStatement()
                                    .executeUpdate(
                                                    "CREATE TABLE IF NOT EXISTS objectives (id INTEGER PRIMARY KEY "
                                                                    + autoIncrement
                                                                    + ", playerID VARCHAR(256) NOT NULL, instructions VARCHAR(2048) NOT NULL, isused BOOLEAN NOT NULL DEFAULT 0);");
                    connection.createStatement().executeUpdate(
                                    "CREATE TABLE IF NOT EXISTS tags (id INTEGER PRIMARY KEY " + autoIncrement
                                                    + ", playerID VARCHAR(256) NOT NULL, tag TEXT NOT NULL, isused BOOLEAN NOT NULL DEFAULT 0);");
                    connection.createStatement().executeUpdate(
                                    "CREATE TABLE IF NOT EXISTS points (id INTEGER PRIMARY KEY " + autoIncrement
                                                    + ", playerID VARCHAR(256) NOT NULL, category VARCHAR(256) NOT NULL, count INT NOT NULL);");
                    connection.createStatement().executeUpdate(
                                    "CREATE TABLE IF NOT EXISTS journal (id INTEGER PRIMARY KEY " + autoIncrement
                                                    + ", playerID VARCHAR(256) NOT NULL, pointer VARCHAR(256) NOT NULL, date TIMESTAMP NOT NULL);");
            } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        closeConnection();
	    }
}