package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.conversation.ConversationData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.Utils;

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
