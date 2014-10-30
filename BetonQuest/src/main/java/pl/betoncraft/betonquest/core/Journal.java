/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;

import com.google.common.collect.Lists;

/**
 * Represents player's journal
 * @author Co0sh
 */
public class Journal {
	
	private List<Pointer> pointers = new ArrayList<Pointer>();
	private List<String> texts = new ArrayList<String>();
	
	public Journal(String playerID) {
		JournalRes res = BetonQuest.getInstance().getJournalRes().get(playerID);
		while (res.next()) {
			pointers.add(new Pointer(res.getRow().getPointer(), res.getRow().getTimestamp()));
		}
		BetonQuest.getInstance().getJournalRes().remove(playerID);
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

	public void clear() {
		texts.clear();
		pointers.clear();
	}
}
