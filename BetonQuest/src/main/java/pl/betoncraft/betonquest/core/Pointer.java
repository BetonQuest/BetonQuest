/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.sql.Timestamp;

/**
 * 
 * @author Co0sh
 */
public class Pointer {

	private final String pointer;
	private final Timestamp timestamp;
	
	public Pointer(String pointer, Timestamp timestamp) {
		this.pointer = pointer;
		this.timestamp = timestamp;
	}

	/**
	 * @return the pointer
	 */
	public String getPointer() {
		return pointer;
	}

	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
}
