/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.sql.Timestamp;

/**
 * Represents one row in ResultSet containing journal data
 * @author Co0sh
 */
public class JournalResRow {

	private Timestamp timestamp;
	private String pointer;
	
	public JournalResRow(Timestamp timestamp, String pointer) {
		this.timestamp = timestamp;
		this.pointer = pointer;
	}

	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the entry
	 */
	public String getPointer() {
		return pointer;
	}
}
