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
public class StringRes {
	
	private List<String> strings = new ArrayList<String>();
	private int iterator;
	
	public StringRes(ResultSet res) {
		try {
			while (res.next()) {
				strings.add(res.getString("string"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		iterator = -1;
	}
	
	/**
	 * Moves cursor to next place and returns if there is anything there
	 * @return
	 */
	public boolean next() {
		iterator++;
		if (iterator < strings.size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * returns string (tag) on this row
	 * @return
	 */
	public String getString() {
		return strings.get(iterator);
	}
}
