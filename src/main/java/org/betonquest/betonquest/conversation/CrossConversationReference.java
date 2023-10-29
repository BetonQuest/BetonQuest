package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;

/**
 * Represent a reference from one conversation to another.
 *
 * @param sourcePack   the referring package
 * @param sourceConv   the referring conversation
 * @param sourceOption the referring option
 * @param targetPack   the referred package
 * @param targetConv   the referred conversation
 * @param targetOption the referred option
 */
public record CrossConversationReference(QuestPackage sourcePack, String sourceConv, String sourceOption,
                                         QuestPackage targetPack, String targetConv, String targetOption) {
}
