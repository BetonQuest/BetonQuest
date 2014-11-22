/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.util.HashMap;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.ConversationListener;
import pl.betoncraft.betonquest.inout.NPCListener;
import pl.betoncraft.betonquest.inout.UnifiedLocation;
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
	private boolean movementBlock;
	
	/**
	 * Constructor method, starts a new conversation between player and npc at given location
	 * @param playerID
	 * @param conversationID
	 */
	public Conversation(String playerID, String conversationID, UnifiedLocation location) {
		
		this.playerID = playerID;
		this.conversationID = conversationID;
		
		// get quester's name
		quester = ConfigInput.getString("conversations." + conversationID + ".quester");
		
		if (quester == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation not defined:" + conversationID);
			return;
		}
		
		// initialize listeners for player's replies
		listener = new ConversationListener(playerID, location, this);
		
		// print message about starting a conversation
		SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages."+ ConfigInput.getString("config.language") +".conversation_start").replaceAll("%quester%", quester));

		// get initial npc's options
		String options = ConfigInput.getString("conversations." + conversationID + ".first");
		
		if (options == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation initialization not defined at: " + conversationID);
			endConversation();
			return;
		}
		
		// if stop is true stop the player from moving away
		String stop = ConfigInput.getString("conversations." + conversationID + ".stop");
		if (stop != null && stop.equalsIgnoreCase("true")) {
			movementBlock = true;
		} else {
			movementBlock = true;
		}
		
		// print one of them
		printNPCText(options);
	}

	private void printNPCText(String options) {
		
		// if options are empty end conversation
		if (options.equals("")) {
			endConversation();
			return;
		}
		
		// get npc's text
		String option = null;
		options:
		for (String NPCoption : options.split(",")) {
			String rawConditions = ConfigInput.getString("conversations." + this.conversationID + ".NPC_options." + NPCoption + ".conditions");
			if (rawConditions == null) {
				endConversation();
				BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " NPC condition error at: " + NPCoption);
				return;
			}
			String[] conditions = rawConditions.split(",");
			for (String condition : conditions) {
				if (condition.equals("")) {
					option = NPCoption;
					break options;
				}
				if (!BetonQuest.condition(this.playerID, condition)) {
					continue options;
				}
			}
			option = NPCoption;
			break;
		}
		
		// if there are no possible options end conversation
		if (option == null) {
			endConversation();
			return;
		}
		
		// and print it to player
		String text = ConfigInput.getString("conversations." + this.conversationID + ".NPC_options." + option + ".text");
		if (text == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " NPC text error at: " + option);
			endConversation();
			return;
		}
		SimpleTextOutput.sendQuesterMessage(this.playerID, quester, text);
		
		String events = ConfigInput.getString("conversations." + this.conversationID + ".NPC_options." + option + ".events");
		if (events == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " NPC event error at: " + option);
			endConversation();
			return;
		}
		fireEvents(events);
		
		String pointers = ConfigInput.getString("conversations." + this.conversationID + ".NPC_options." + option + ".pointer");
		if (pointers == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " NPC pointer error at: " + option);
			endConversation();
			return;
		}
		printOptions(pointers);
	}
	
	/**
	 * this method passes given string as answer from player in a conversation
	 * @param rawAnswer
	 */
	public void passPlayerAnswer(String rawAnswer) {
		
		String answer = rawAnswer.trim();
		
		// if answer isn't a number, or the number is greater than amount of possible options then print messages
		if (answer.equalsIgnoreCase("0") || !answer.matches("\\d+") || Integer.valueOf(answer) > current.size()) {
			// some text from npc saying that he doesn't understand player
			String message = ConfigInput.getString("conversations." + conversationID + ".unknown");
			if (message == null) {
				BetonQuest.getInstance().getLogger().severe("Conversation unknown line not defined at:" + conversationID);
				endConversation();
				return;
			}
			SimpleTextOutput.sendQuesterMessage(playerID, quester, message);
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
		String reply = ConfigInput.getString("conversations." + conversationID + ".player_options." + choosenAnswerID + ".text");
		if (reply == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " player text error at: " + choosenAnswerID);
			endConversation();
			return;
		}
		SimpleTextOutput.sendPlayerReply(playerID, quester, reply);
		
		// fire events
		String events = ConfigInput.getString("conversations." + conversationID + ".player_options." + choosenAnswerID + ".events");
		if (events == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " player event error at: " + choosenAnswerID);
			endConversation();
			return;
		}
		fireEvents(events);
				
		// print to player npc's answer
		String NPCanswer = ConfigInput.getString("conversations." + conversationID + ".player_options." + choosenAnswerID + ".pointer");
		if (NPCanswer == null) {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " player pointer error at: " + choosenAnswerID);
			endConversation();
			return;
		}
		printNPCText(NPCanswer);
	}

	private void fireEvents(String rawEvents) {
		// do nothing if its empty
		if (!rawEvents.equalsIgnoreCase("")) {
			// split it to individual event ids
			String[] events = rawEvents.split(",");
			// foreach eventID fire an event
			for (String event : events) {
				BetonQuest.event(playerID, event);
			}
		}
	}

	/**
	 * prints options the player have
	 * @param rawOptions
	 */
	private void printOptions(String rawOptions) {
		
		// if rawOptions are empty
		if (rawOptions.equals("")) {
			endConversation();
			return;
		}
		
		// get IDs
		String[] options = rawOptions.split(",");
		
		//print them
		int i = 0;
		answers:
		for (String option : options) {
			// get conditions from config
			String rawConditions = ConfigInput.getString("conversations." + conversationID + ".player_options." + option + ".conditions");
			if (rawConditions == null) {
				BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " player conditions error at: " + option);
				endConversation();
				return;
			}
			// if there are any conditions, do something with them
			if (!rawConditions.equalsIgnoreCase("")) {
				// split them to separate ids
				String[] conditions = rawConditions.split(",");
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
			String reply = ConfigInput.getString("conversations." + conversationID + ".player_options." + option + ".text");
			if (reply == null) {
				BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " player text error at: " + option);
				endConversation();
				return;
			}
			SimpleTextOutput.sendQuesterReply(playerID, i, quester, reply);
			// put reply to hashmap in order to find it's ID when player responds by it's i number (id is string, we don't want to print it to player)
			current.put(Integer.valueOf(i), option);
		}
		
		// end conversations if there are no possible options
		if (current.isEmpty()) {
			endConversation();
			return;
		}
	}
	
	/**
	 * ends conversation...
	 */
	public void endConversation() {
		// fire final events
		String rawFinalEvents = ConfigInput.getString("conversations." + conversationID + ".final_events");
		if (rawFinalEvents != null) {
			if (!rawFinalEvents.equals("")) {
				String[] finalEvents = rawFinalEvents.split(",");
				for (String event : finalEvents) {
					BetonQuest.event(playerID, event);
				}
			}
		} else {
			BetonQuest.getInstance().getLogger().severe("Conversation " + conversationID + " final events error!");
		}
		// print message
		SimpleTextOutput.sendSystemMessage(playerID, ConfigInput.getString("messages."+ ConfigInput.getString("config.language") +".conversation_end").replaceAll("%quester%", quester));
		// delete conversation
		NPCListener.removePlayerConversation(playerID);
		// unregister listener
		listener.unregisterListener();
	}

	public boolean isMovementBlock() {
		return movementBlock;
	}

}
