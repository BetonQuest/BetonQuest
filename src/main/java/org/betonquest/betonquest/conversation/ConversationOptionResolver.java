package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ConversationID;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves a string to a {@link ConversationData} instance and the name of the option.
 */
public class ConversationOptionResolver {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The {@link ConversationData.OptionType} of the option.
     */
    private final ConversationData.OptionType optionType;

    /**
     * The package from which we are searching for the conversation.
     */
    private final QuestPackage pack;

    /**
     * The name of the option that is searched.
     */
    @Nullable
    private final String optionName;

    /**
     * The name of the conversation that is searched.
     */
    private final String convName;

    /**
     * Prepares the given information for resolving a conversation option inside a conversation.
     * Use {@link #resolve()} to resolve the information.
     *
     * @param featureApi              the feature API
     * @param currentPackage          the package from which we are searching for the conversation
     * @param currentConversationName the current conversation data
     * @param optionType              the {@link ConversationData.OptionType} of the option
     * @param option                  the option string to resolve
     * @throws QuestException when the option string is incorrectly formatted or
     *                        when the conversation could not be found
     */
    public ConversationOptionResolver(final FeatureApi featureApi, final QuestPackage currentPackage,
                                      final String currentConversationName, final ConversationData.OptionType optionType,
                                      final String option) throws QuestException {
        this.featureApi = featureApi;
        this.optionType = optionType;

        final String[] parts = option.split("\\.", -1);
        switch (parts.length) {
            // Either "pack.Conv." (Other package, user specified only the conversation but not the option,
            // therefore we need to select the "starting options". This means the optionName must be null.)
            // Or "pack.Conv.option" (different conversation, different package)
            case 3 -> {
                final ConversationID conversationID = new ConversationID(currentPackage, parts[0] + "." + parts[1]);
                pack = conversationID.getPackage();
                convName = parts[1];
                optionName = parts[2].isEmpty() ? null : parts[2];
            }
            // "Conv.option" (Same package but different conversation)
            case 2 -> {
                pack = currentPackage;
                convName = parts[0];
                optionName = parts[1];
            }
            // "option" (Same conversation, same package)
            case 1 -> {
                pack = currentPackage;
                convName = currentConversationName;
                optionName = parts[0];
            }
            default -> throw new QuestException("Invalid conversation pointer format in package '"
                    + currentPackage.getQuestPath() + "', conversation '" + currentConversationName + "': " + option);
        }
    }

    /**
     * Resolves the given information to a {@link ResolvedOption}.
     *
     * @return a {@link ResolvedOption}
     * @throws QuestException when the conversation containing the option could not be found
     */
    public ResolvedOption resolve() throws QuestException {
        final ConversationID conversationWithNextOption = new ConversationID(pack, convName);

        //Since the conversation might be in another package we must load this again
        final ConversationData newData = featureApi.getConversation(conversationWithNextOption);
        return new ResolvedOption(newData, optionType, optionName);
    }
}
