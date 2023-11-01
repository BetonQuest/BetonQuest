package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.builder.ConversationIDBuilder;

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

        if (instruction.next() == null) {
            throw new InstructionParseException("Missing conversation parameter.");
        }

        conversationID = new ConversationIDBuilder(instruction.getPackage(), instruction.current()).build();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        try {
            return BetonQuest.getInstance().getConversation(conversationID).isReady(profile);
        } catch (final InstructionParseException e) {
            throw new QuestRuntimeException("External pointers in the conversation this condition checks for could not"
                    + " be resoled during runtime.", e);
        }
    }
}
