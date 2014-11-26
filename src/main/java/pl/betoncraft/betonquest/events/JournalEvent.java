/**
 * 
 */
package pl.betoncraft.betonquest.events;

import java.sql.Timestamp;
import java.util.Date;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Pointer;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.JournalBook;
import pl.betoncraft.betonquest.inout.SimpleTextOutput;

/**
 * 
 * @author Co0sh
 */
public class JournalEvent extends QuestEvent {

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public JournalEvent(String playerID, String instructions) {
		super(playerID, instructions);
		BetonQuest.getInstance().getJournal(playerID).addPointer(new Pointer(instructions.split(" ")[1], new Timestamp(new Date().getTime())));
		JournalBook.updateJournal(playerID);
		SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".new_journal_entry"));
	}
	
}
