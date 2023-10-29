package org.betonquest.betonquest.conversation;

/**
 * Simple record that represents one option inside a conversation.
 *
 * @param conversationData the data of the conversation that contains the option
 * @param type             the {@link org.betonquest.betonquest.conversation.ConversationData.OptionType} of the option
 * @param optionName       the name of the option
 */
public record ConversationOptionResolverResult(ConversationData conversationData, ConversationData.OptionType type,
                                               String optionName) {
}
