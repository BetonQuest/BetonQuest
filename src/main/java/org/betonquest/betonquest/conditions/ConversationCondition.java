package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;

/**
 * Checks if the conversation with player has at least one possible option
 * <p>
 * Example:
 * {@code conversation <name of conversation>}
 **/
@SuppressWarnings("PMD.CommentRequired")
public class ConversationCondition extends Condition {

    /**
     * The conversation to check.
     */
    private final ConversationID conversationID;

    /**
     * Creates a new ConversationCondition.
     *
     * @param instruction the user-provided instruction to parse
     * @throws InstructionParseException if the instruction is invalid
     */
    public ConversationCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        try {
            conversationID = new ConversationID(instruction.getPackage(), instruction.next());
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException(e.getMessage(), e);
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        try {
            final ConversationData conversation = BetonQuest.getInstance().getConversation(conversationID);
            if (conversation == null) {
                throw new QuestRuntimeException("Tried to check conversation '" + conversationID.getFullID()
                        + "' but it is not loaded! Check for errors on /bq reload!");
            }
            return conversation.isReady(profile);
        } catch (InstructionParseException | ObjectNotFoundException e) {
            throw new QuestRuntimeException("External pointers in the conversation this condition checks for could not"
                    + " be resoled during runtime.", e);
        }
    }
}
