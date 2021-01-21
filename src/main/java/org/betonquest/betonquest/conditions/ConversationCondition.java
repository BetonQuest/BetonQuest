package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Checks if the conversation with player has at least one possible option
 * <p>
 * Example:
 * {@code conversation <name of conversation>}
 **/
@SuppressWarnings("PMD.CommentRequired")
public class ConversationCondition extends Condition {

    private final String conversationID;

    public ConversationCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);

        if (instruction.next() == null) {
            throw new InstructionParseException("Missing conversation parameter");
        }

        conversationID = instruction.current();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {

        final ConversationData conversation = BetonQuest.getInstance().getConversation(Utils.addPackage(instruction.getPackage(), conversationID));

        if (conversation == null) {
            throw new QuestRuntimeException("Conversation does not exist: " + instruction.getPackage().getName() + conversationID);
        }

        return conversation.isReady(playerID);
    }

}
