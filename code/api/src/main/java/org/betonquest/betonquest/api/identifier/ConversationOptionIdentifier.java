package org.betonquest.betonquest.api.identifier;

import org.jetbrains.annotations.Nullable;

/**
 * A conversation option identifier pointing to a specific option inside a conversation.
 */
public interface ConversationOptionIdentifier extends Identifier {

    /**
     * Get the name of the conversation this option belongs to, or null if it is in the current conversation.
     *
     * @return the conversation name or null
     */
    @Nullable
    String getConversationName();

    /**
     * Get the name of the option, or null if it is not specified.
     *
     * @return the option name or null
     */
    @Nullable
    String getOptionName();
}
