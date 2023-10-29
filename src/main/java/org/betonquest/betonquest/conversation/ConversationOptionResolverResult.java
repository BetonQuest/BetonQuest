package org.betonquest.betonquest.conversation;

/**
 * Simple record that represents one option inside a conversation.
 *
 * @param conversationData the data of the conversation that contains the option
 * @param optionName       the name of the option
 */
public record ConversationOptionResolverResult(ConversationData conversationData, String optionName) {
}
