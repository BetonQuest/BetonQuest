package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents all data of a conversation.
 */
public interface ConversationData {

    /**
     * Checks if external pointers point to valid options. This cannot be checked
     * when constructing {@link ConversationData} objects because conversations that are
     * being pointed to may not yet exist.
     * <p>
     * This method should be called when all conversations are loaded.
     *
     * @throws QuestException when a pointer to an external conversation could not be resolved
     */
    void checkExternalPointers() throws QuestException;

    /**
     * Resolves a pointer to an option in a conversation.
     *
     * @param conversationOptionID the option string to resolve
     * @param optionType           the {@link ConversationOptionType} of the option
     * @return a {@link ResolvedOption} pointing to the option
     * @throws QuestException when the conversation could not be resolved
     */
    ResolvedOption resolveOption(ConversationOptionIdentifier conversationOptionID, ConversationOptionType optionType) throws QuestException;

    /**
     * Returns all addresses of options that are available after the provided option is selected.
     *
     * @param profile the profile of the player to get the pointers for
     * @param option  the option to get the pointers for
     * @return a list of pointer addresses
     */
    List<String> getPointers(Profile profile, ResolvedOption option);

    /**
     * Get the public data.
     *
     * @return external used data
     */
    ConversationPublicData getPublicData();

    /**
     * Returns a list of all option names that the conversation can start from.
     *
     * @return a list of all option names
     */
    List<String> getStartingOptions();

    /**
     * Gets the text of the specified option in the specified language.
     * Respects extended options.
     *
     * @param profile the profile of the player
     * @param option  the option
     * @return the text of the specified option in the specified language
     */
    @Nullable
    Component getText(@Nullable Profile profile, ResolvedOption option);

    /**
     * Gets the properties of the specified option.
     * This is a section that can contain any properties defined by the conversation.
     *
     * @param profile the profile of the player
     * @param option  the option
     * @return the properties of the specified option
     */
    ConfigurationSection getProperties(@Nullable Profile profile, ResolvedOption option);

    /**
     * Gets the package containing this conversation.
     *
     * @return the package containing this conversation
     */
    QuestPackage getPack();

    /**
     * Gets the conditions required for the specified option to be selected.
     *
     * @param option the conversation option
     * @param type   the type of the option
     * @return the conditions required for the specified option to be selected
     */
    List<ConditionIdentifier> getConditionIDs(String option, ConversationOptionType type);

    /**
     * Gets the actions that will be executed when the specified option is selected.
     *
     * @param profile the profile of the player
     * @param option  the name of the conversation option
     * @param type    the type of the option
     * @return a list of {@link ActionIdentifier}s
     */
    List<ActionIdentifier> getActionIDs(Profile profile, ResolvedOption option, ConversationOptionType type);

    /**
     * Checks if the conversation can start for the given player. This means it must have at least one option with
     * conditions that are met by the player.
     *
     * @param profile the {@link Profile} of the player
     * @return True, if the player can star the conversation.
     * @throws QuestException if an external pointer reference has an invalid format or
     *                        if an external pointer inside the conversation could not be resolved
     */
    boolean isReady(Profile profile) throws QuestException;
}
