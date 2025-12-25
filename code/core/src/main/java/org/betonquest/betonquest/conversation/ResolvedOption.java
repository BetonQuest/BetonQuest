package org.betonquest.betonquest.conversation;

import org.jetbrains.annotations.Nullable;

/**
 * Simple record that represents one option inside a conversation.
 *
 * @param conversationData the data of the conversation that contains the option
 * @param type             the {@link org.betonquest.betonquest.conversation.ConversationData.OptionType} of the option
 * @param name             the name of the option as defined in the conversation config
 */
public record ResolvedOption(ConversationData conversationData, ConversationData.OptionType type,
                             @Nullable String name) {

}
