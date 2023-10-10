package org.betonquest.betonquest.id.builder;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.QuestCancelerID;

/**
 * A simple builder for {@link QuestCancelerID}.
 */
public class QuestCancelerIDBuilder {

    /*
     * Really only exists to ensure a consistent log message in case of errors. Missing conversation have been a huge
     * problem for users.
     */

    /**
     * The package the conversation is in.
     */
    private final QuestPackage pack;
    /**
     * The id of the conversation.
     */
    private final String identifier;

    /**
     * Creates a new ConversationIDBuilder.
     *
     * @param pack       the package the conversation is in
     * @param identifier the id of the conversation
     */
    public QuestCancelerIDBuilder(final QuestPackage pack, final String identifier) {
        this.pack = pack;
        this.identifier = identifier;
    }

    /**
     * Builds the {@link ConversationID}.
     *
     * @return the conversation id if it exists
     * @throws InstructionParseException if the conversation does not exist
     */
    public QuestCancelerID build() throws InstructionParseException {
        try {
            return new QuestCancelerID(pack, identifier);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Quest canceler '" + pack.getQuestPath() + "." + identifier + "' does not exist. Make sure you have" +
                    " no typos in the quest canceler's name. Alternatively the quest canceler contains an error and could not" +
                    " be loaded after the reload. Check the output of /bq reload to see errors regarding this quest canceler.", e);
        }
    }
}
