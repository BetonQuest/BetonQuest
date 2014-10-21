/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.util.HashMap;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.ConversationListener;
import pl.betoncraft.betonquest.inout.NPCListener;
import pl.betoncraft.betonquest.inout.NPCLocation;
import pl.betoncraft.betonquest.inout.SimpleTextOutput;

/**
 * Represents a conversation between QuestPlayer and Quester
 * @author Co0sh
 */
public class Conversation {
	
	private final String quester;
	private final String playerID;
	private final String conversationID;
	private HashMap<Integer,String> current = new HashMap<Integer,String>();
	private ConversationListener listener;
	
	/**
	 * Constructor method, starts a new conversation between player and npc at given location
	 * @param playerID
	 * @param conversationID
	 */
	public Conversation(String playerID, String conversationID, NPCLocation location) {
		
		// get quester's name
		quester = ConfigInput.getString("conversations." + conversationID + ".quester");
		this.playerID = playerID;
		this.conversationID = conversationID;
		
		// print message about starting a conversation
		SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages."+ ConfigInput.getString("config.language") +".conversation_start").replaceAll("%quester%", quester));

		// get initial npc's text
		String initial = ConfigInput.getString("conversations." + conversationID + ".initial");
		
		// and print it to player
		SimpleTextOutput.sendQuesterMessage(playerID, quester, initial);
		
		getStartingPoint();
		
		// initialize listeners for player's replies
		listener = new ConversationListener(playerID, location, this);
	}

	/**
	 * returns to the starting point
	 */
	private void getStartingPoint() {
		String options = ConfigInput.getString("conversations." + conversationID + ".first");
		printOptions(options);
	}
	
	/**
	 * this method passes given string as answer from player in a conversation
	 * @param rawAnswer
	 */
	public void passPlayerAnswer(String rawAnswer) {
		
		String answer = rawAnswer.trim();
		
		// if answer isn't a number, or the number is greater than amount of possible options then print messages
		if (!answer.matches("\\d+") || Integer.valueOf(answer) > current.size()) {
			// some text from npc saying that he doesn't understand player
			SimpleTextOutput.sendQuesterMessage(playerID, quester, ConfigInput.getString("conversations." + conversationID + ".unknown"));
			// and instructions from plugin about answering npcs
			SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".help_with_answering"));
			return;
		}
		
		// get the answer ID from player's response
		Integer number = new Integer(answer);
		String choosenAnswerID = current.get(number);
		
		// clear hashmap
		current.clear();
		
		// print to player his answer
		SimpleTextOutput.sendPlayerReply(playerID, quester, ConfigInput.getString("conversations." + conversationID + ".options." + choosenAnswerID + ".question"));
		
		// print to player npc's answer
		SimpleTextOutput.sendQuesterMessage(playerID, quester, ConfigInput.getString("conversations." + conversationID + ".options." + choosenAnswerID + ".answer"));
		
		// get raw event string
		String rawEvents = ConfigInput.getString("conversations." + conversationID + ".options." + choosenAnswerID + ".events");
		// do nothing if its empty
		if (!rawEvents.equalsIgnoreCase("")) {
			// split it to individual event ids
			String[] events = rawEvents.split(",");
			// foreach eventID fire an event
			for (String event : events) {
				BetonQuest.event(playerID, event);
			}
		}
		
		// read answering options
		String rawOptions = ConfigInput.getString("conversations." + conversationID + ".options." + choosenAnswerID + ".pointer");
		
		// end conversation if there's no pointers
		if (rawOptions.equalsIgnoreCase("")) {
			endConversation();
			return;
		}
		
		// return to starting point if the pointer is 0
		if (rawOptions.equals("0")) {
			getStartingPoint();
			return;
		}
		
		// print options to player
		printOptions(rawOptions);
		
		return;
	}

	/**
	 * prints options the player have
	 * @param rawOptions
	 */
	private void printOptions(String rawOptions) {
		// else get pointed IDs
		String[] options = rawOptions.split(",");
		
		//print them
		int i = 0;
		answers:
		for (String option : options) {
			// get conditions from config
			String rawConditions = ConfigInput.getString("conversations." + conversationID + ".options." + option + ".conditions");
			// if there are any conditions, do something with them
			if (!rawConditions.equalsIgnoreCase("")) {
				// split them to separate ids
				String[] conditions = ConfigInput.getString("conversations." + conversationID + ".options." + option + ".conditions").split(",");
				// if some condition is not met, skip printing this option and move on
				for (String conditionID : conditions) {
					if (!BetonQuest.condition(playerID, conditionID)) {
						continue answers;
					}
				}
			}
			// i is for counting replies, like 1. something, 2. something else etc.
			i++;
			// print reply
			SimpleTextOutput.sendQuesterReply(playerID, i, quester, ConfigInput.getString("conversations." + conversationID + ".options." + option + ".question"));
			// put reply to hashmap in order to find it's ID when player responds by it's i number (id is string, we don't want to print it to player)
			current.put(Integer.valueOf(i), option);
		}
	}
	
	/**
	 * ends conversation...
	 */
	public void endConversation() {
		// print message
		SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages."+ ConfigInput.getString("config.language") +".conversation_end").replaceAll("%quester%", quester));
		// delete conversation
		NPCListener.removePlayerConversation(playerID);
		// unregister listener
		listener.unregisterListener();
	}

}
