package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.builder.ConversationIDBuilder;

/**
 * Resolves a string to a {@link ConversationData} instance and the name of the option.
 */
public class ConversationOptionResolver {

    /**
     * The {@link BetonQuest} instance.
     */
    private final BetonQuest plugin;

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
    private final String optionName;

    /**
     * The name of the conversation that is searched.
     */
    private final String convName;

    /**
     * Prepares the given information for resolving a conversation option inside a conversation.
     * Use {@link #resolve()} to resolve the information.
     *
     * @param plugin                  the plugin instance
     * @param currentPackage          the package from which we are searching for the conversation
     * @param currentConversationName the current conversation data
     * @param optionType              the {@link ConversationData.OptionType} of the option
     * @param option                  the option string to resolve
     */
    public ConversationOptionResolver(final BetonQuest plugin, final QuestPackage currentPackage, final String currentConversationName, final ConversationData.OptionType optionType, final String option) throws InstructionParseException {
        this.plugin = plugin;
        this.optionType = optionType;

        final String[] parts = option.split("\\.");
        switch (parts.length) {
            // Different conversation, different package
            // pack.Conv.option
            case 3 -> {
                final ConversationID conversationID = new ConversationIDBuilder(currentPackage, parts[0] + "." + parts[1]).build();
                pack = conversationID.getPackage();
                convName = parts[1];
                optionName = parts[2];
            }
            // Either pack.Conv/ (= Other package, user specified only the conversation but not the option,
            // therefore we need to select the "starting options". This means optionName = null.)
            // Or Conv.option (= Same package but different conversation)
            case 2 -> {
                if (option.contains("/")) {
                    final ConversationID conversationID = new ConversationIDBuilder(currentPackage, parts[0] + "." + parts[1]).build();
                    pack = conversationID.getPackage();
                    convName = parts[0];
                    optionName = null;
                } else {
                    pack = currentPackage;
                    convName = parts[0];
                    optionName = parts[1];
                }
            }
            // Same conversation, same package
            case 1 -> {
                pack = currentPackage;
                convName = currentConversationName;
                optionName = parts[0];
            }
            default -> throw new InstructionParseException("Invalid conversation pointer format in package '"
                    + currentPackage.getQuestPath() + "', conversation '" + currentConversationName + "':" + option);
        }
    }

    /**
     * Resolves the given information to a {@link ResolvedOption}.
     *
     * @return a {@link ResolvedOption}
     * @throws InstructionParseException when the conversation could not be resolved
     */
    public ResolvedOption resolve() throws InstructionParseException {
        final ConversationID conversationWithNextOption = new ConversationIDBuilder(pack, convName).build();

        //Since the conversation might be in another package we must load this again
        final ConversationData newData = plugin.getConversation(conversationWithNextOption);
        return new ResolvedOption(newData, optionType, optionName);
    }

}
