package org.betonquest.betonquest.api.config.quest;

/**
 * Functionality for a quest to get all related information.
 */
@SuppressWarnings("PMD.CommentRequired")
public interface QuestPackage extends QuestTemplate {
    @Deprecated
    String getRawString(String address);

    @Deprecated
    String subst(String input);

    @Deprecated
    String getString(String address);

    @Deprecated
    String getString(String address, String def);

    @Deprecated
    String getFormattedString(String address);
}
