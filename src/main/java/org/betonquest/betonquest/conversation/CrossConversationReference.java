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
 * @param resolver     the resolver that will be used to resolve the conversation option
 */
public record CrossConversationReference(QuestPackage sourcePack, String sourceConv, @Nullable String sourceOption,
                                         ConversationOptionResolver resolver) {
}
