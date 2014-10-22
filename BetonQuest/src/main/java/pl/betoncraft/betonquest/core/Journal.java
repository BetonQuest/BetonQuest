/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;

/**
 * Represents player's journal
 * @author Co0sh
 */
public class Journal {
	
	private List<Pointer> pointers = new ArrayList<Pointer>();
	private List<String> texts = new ArrayList<String>();
	
	public Journal(String playerID) {
		BetonQuest.getInstance().getMySQL().openConnection();
		ResultSet res = BetonQuest.getInstance().getMySQL().querySQL("SELECT pointer, date FROM journal WHERE playerID = '" + playerID + "'");
		try {
			while (res.next()) {
				pointers.add(new Pointer(res.getString("pointer"), res.getTimestamp("date")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BetonQuest.getInstance().getMySQL().closeConnection();
		generateTexts();
	}
	
	public List<Pointer> getPointers() {
		return pointers;
	}
	
	public void addPointer(Pointer pointer) {
		pointers.add(pointer);
		generateTexts();
	}
	
	public List<String> getText() {
		return Lists.reverse(texts);
	}
	
	private void generateTexts() {
		texts.clear();
		for (Pointer pointer : pointers) {
			String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(pointer.getTimestamp());
			texts.add(date + "\n" + ConfigInput.getString("journal." + pointer.getPointer()));
		}
	}

}
