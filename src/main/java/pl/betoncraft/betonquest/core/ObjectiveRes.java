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
public class ObjectiveRes {

	private List<String> instructions = new ArrayList<String>();
	private int iterator;
	
	public ObjectiveRes(ResultSet res) {
		try {
			while (res.next()) {
				instructions.add(res.getString("instructions"));
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
		if (iterator < instructions.size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * returns instruction string for objective on this row
	 * @return
	 */
	public String getInstruction() {
		return instructions.get(iterator);
	}
}
