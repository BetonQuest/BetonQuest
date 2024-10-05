package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;

/**
 * A factory for creating ConversationCondition objects.
 */
public class ConversationConditionFactory implements PlayerConditionFactory {

    /**
     * Creates a new ConversationConditionFactory.
     */
    public ConversationConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        try {
            final ConversationID conversationID = new ConversationID(instruction.getPackage(), instruction.next());
            return new ConversationCondition(conversationID);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException(e.getMessage(), e);
        }
    }
}
