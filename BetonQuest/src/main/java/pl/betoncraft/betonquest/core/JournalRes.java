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
public class JournalRes {
	
	private List<JournalResRow> rows = new ArrayList<JournalResRow>();
	private int iterator;
	
	public JournalRes(ResultSet res) {
		try {
			while (res.next()) {
				rows.add(new JournalResRow(res.getTimestamp("date"), res.getString("pointer")));
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
		if (iterator < rows.size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns row representing one journal entry
	 * @return
	 */
	public JournalResRow getRow() {
		return rows.get(iterator);
	}

}
