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
	
	private PreparedStatement 
					ping,
					
					tableObjectives,
					tableTags,
					tablePoints,
					tableJournal,
					
					addNewObjective,
					deleteUsedObjectives,
					deletePoints,
					addPoints,
					deleteTags,
					addTags,
					deleteJournal,
					addJournal,
					updateObjectives,
					updateTags,
					
					deleteAllObjectives,
					
					selectUsedObjectives,
					selectUnusedObjectives,
					selectUsedTags,
					selectUnusedTags,
					selectJounral,
					selectPoints;
	
	protected Database(Plugin plugin) {
		this.plugin = plugin;
		this.connection = null;
	}
	
	public abstract Connection openConnection();
	
	public void generateStatements() {
		openConnection();
		try {
			ping = connection.prepareStatement("SELECT 1;");
			addNewObjective = connection.prepareStatement("INSERT INTO objectives (playerID, instructions, isused) VALUES (?, ?, 0);");
			deleteUsedObjectives = connection.prepareStatement("DELETE FROM objectives WHERE playerID = ? AND isused = 1;");
			deletePoints = connection.prepareStatement("DELETE FROM points WHERE playerID = ?;");
			addPoints = connection.prepareStatement("INSERT INTO points (playerID, category, count) VALUES (?, ?, ?);");
			deleteTags = connection.prepareStatement("DELETE FROM tags WHERE playerID = ?;");
			addTags = connection.prepareStatement("INSERT INTO tags (playerID, tag) VALUES (?, ?);");
			deleteJournal = connection.prepareStatement("DELETE FROM journal WHERE playerID = ?;");
			addJournal = connection.prepareStatement("INSERT INTO journal (playerID, pointer, date) VALUES (?, ?, ?);");
			deleteAllObjectives = connection.prepareStatement("DELETE FROM objectives WHERE playerID = ?;");
			updateObjectives = connection.prepareStatement("UPDATE objectives SET isused = 1 WHERE playerID = ? AND isused = 0;");
			updateTags = connection.prepareStatement("UPDATE tags SET isused = 1 WHERE playerID = ? AND isused = 0;");
			selectUsedObjectives = connection.prepareStatement("SELECT instructions FROM objectives WHERE playerID = ? AND isused= 1;");
			selectUnusedObjectives = connection.prepareStatement("SELECT instructions FROM objectives WHERE playerID = ? AND isused= 0;");
			selectUsedTags = connection.prepareStatement("SELECT tag FROM tags WHERE playerID = ? AND isused = 1;");
			selectUnusedTags = connection.prepareStatement("SELECT tag FROM tags WHERE playerID = ? AND isused = 0;");
			selectJounral = connection.prepareStatement("SELECT pointer, date FROM journal WHERE playerID = ?;");
			selectPoints = connection.prepareStatement("SELECT category, count FROM points WHERE playerID = ?;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet querySQL(QueryType type, String[] args) {
		try {
			ping.executeQuery();
		} catch (SQLException e) {
			openConnection();
		}
		try {
			PreparedStatement statement;
			switch (type) {
			case SELECT_JOURNAL:
				statement = selectJounral;
				break;
			case SELECT_POINTS:
				statement = selectPoints;
				break;
			case SELECT_UNUSED_OBJECTIVES:
				statement = selectUnusedObjectives;
				break;
			case SELECT_UNUSED_TAGS:
				statement = selectUnusedTags;
				break;
			case SELECT_USED_OBJECTIVES:
				statement = selectUsedObjectives;
				break;
			case SELECT_USED_TAGS:
				statement = selectUsedTags;
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
			ping.executeQuery();
		} catch (SQLException e) {
			openConnection();
		}
		try {
			PreparedStatement statement;
			switch (type) {
			case TABLE_OBJECTIVE:
				statement = this.tableObjectives;
				break;
			case TABLE_JOURNAL:
				statement = this.tableJournal;
				break;
			case TABLE_POINTS:
				statement = this.tablePoints;
				break;
			case TABLE_TAGS:
				statement = this.tableTags;
				break;
			case DELETE_USED_OBJECTIVES:
				statement = this.deleteUsedObjectives;
				break;
			case DELETE_POINTS:
				statement = this.deletePoints;
				break;
			case ADD_NEW_OBJECTIVE:
				statement = this.addNewObjective;
				break;
			case ADD_POINTS:
				statement = this.addPoints;
				break;
			case DELETE_TAGS:
				statement = this.deleteTags;
				break;
			case ADD_TAGS:
				statement = this.addTags;
				break;
			case DELETE_JOURNAL:
				statement = this.deleteJournal;
				break;
			case ADD_JOURNAL:
				statement = this.addJournal;
				break;
			case DELETE_ALL_OBJECTIVES:
				statement = this.deleteAllObjectives;
				break;
			case UPDATE_OBJECTIVES:
				statement = this.updateObjectives;
				break;
			case UPDATE_TAGS:
				statement = this.updateTags;
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
}