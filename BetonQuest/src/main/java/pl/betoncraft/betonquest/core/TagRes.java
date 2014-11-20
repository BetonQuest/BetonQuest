/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts ResultSet to readable and persistent data object. Works exactly the same as ResultSet
 * @author Co0sh
 */
public class TagRes {
	
	private List<String> tags = new ArrayList<String>();
	private int iterator;
	
	public TagRes(ResultSet res) {
		try {
			while (res.next()) {
				tags.add(res.getString("tag"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		iterator = -1;
	}
	
	/**
	 * Moves cursor to next place and returns true if there is anything there
	 * @return
	 */
	public boolean next() {
		iterator++;
		if (iterator < tags.size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * returns string (tag) on this row
	 * @return
	 */
	public String getTag() {
		return tags.get(iterator);
	}
}
