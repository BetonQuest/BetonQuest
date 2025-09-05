package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.jetbrains.annotations.Nullable;

/**
 * Represent a reference from one conversation to another.
 * The resolver will be used after loading all packages to resolve the conversation option in another package.
 *
 * @param sourcePack   the referring package
 * @param sourceConv   the referring conversation
 * @param sourceOption the referring option
 * @param optionType   the type of option that is referring
 * @param resolver     the resolver that will be used to resolve the conversation option
 */
public record CrossConversationReference(QuestPackage sourcePack, ConversationID sourceConv,
                                         @Nullable String sourceOption, ConversationData.OptionType optionType,
                                         ConversationOptionID resolver) {
}
