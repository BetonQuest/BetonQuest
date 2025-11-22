package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a conversation option ID.
 */
public class ConversationOptionID extends Identifier {
    /**
     * The name of the conversation this option belongs to, or null if it is in the current conversation.
     */
    @Nullable
    private final String conversationName;

    /**
     * The name of the option, or null if not specified.
     */
    @Nullable
    private final String optionName;

    /**
     * Creates a new Identifier that handles relative and absolute paths.
     *
     * @param packManager the package manager to resolve packages
     * @param pack        the package the ID is in
     * @param identifier  the identifier string leading to the object
     * @throws QuestException if the identifier could not be parsed
     */
    protected ConversationOptionID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(packManager, pack, identifier);

        final String[] parts = super.get().split("\\.", -1);
        switch (parts.length) {
            case 2 -> {
                conversationName = parts[0];
                optionName = parts[1].isEmpty() ? null : parts[1];
            }
            case 1 -> {
                conversationName = null;
                optionName = parts[0];
            }
            default -> throw new QuestException("Invalid conversation option: " + identifier);
        }
    }

    /**
     * Get the name of the conversation this option belongs to, or null if it is in the current conversation.
     *
     * @return the conversation name or null
     */
    @Nullable
    public String getConversationName() {
        return conversationName;
    }

    /**
     * Get the name of the option, or null if it is not specified.
     *
     * @return the option name or null
     */
    @Nullable
    public String getOptionName() {
        return optionName;
    }
}
