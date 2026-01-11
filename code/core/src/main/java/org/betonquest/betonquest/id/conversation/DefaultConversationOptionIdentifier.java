package org.betonquest.betonquest.id.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.jetbrains.annotations.Nullable;

/**
 * A default implementation of the {@link ConversationOptionIdentifier}.
 */
public class DefaultConversationOptionIdentifier extends DefaultIdentifier implements ConversationOptionIdentifier {

    /**
     * The name of the conversation this option belongs to.
     */
    @Nullable
    private final String conversationName;

    /**
     * The name of the option in the conversation.
     */
    @Nullable
    private final String optionName;

    /**
     * Creates a new instance of the {@link DefaultConversationOptionIdentifier}.
     *
     * @param pack             the package this identifier belongs to
     * @param identifier       the identifier of the option
     * @param conversationName the name of the conversation this option belongs to
     * @param optionName       the name of the option in the conversation
     */
    protected DefaultConversationOptionIdentifier(final QuestPackage pack, final String identifier,
                                                  @Nullable final String conversationName, @Nullable final String optionName) {
        super(pack, identifier);
        this.conversationName = conversationName;
        this.optionName = optionName;
    }

    @Override
    @Nullable
    public String getConversationName() {
        return conversationName;
    }

    @Override
    @Nullable
    public String getOptionName() {
        return optionName;
    }
}
