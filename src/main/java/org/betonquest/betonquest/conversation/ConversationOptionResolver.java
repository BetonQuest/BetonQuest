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
     * The package from which we are searching for the conversation.
     */
    private QuestPackage pack;

    /**
     * The name of the option that is searched.
     */
    private String optionName;

    /**
     * The name of the conversation that is searched.
     */
    private String convName;

    /**
     * Prepares the given information for resolving a conversation option inside a conversation.
     * Use {@link #resolve()} to resolve the information.
     *
     * @param plugin                  the plugin instance
     * @param currentPackage          the package from which we are searching for the conversation
     * @param currentConversationName the current conversation data
     * @param option                  the option string to resolve
     */
    public ConversationOptionResolver(final BetonQuest plugin, final QuestPackage currentPackage, final String currentConversationName, final String option) throws InstructionParseException {
        this.plugin = plugin;

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
            // Either pack.Conv/ (= Other package, user specified only the conversation but not the option, so we use the "starting options")
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
        }
    }

    /**
     * Resolves the given information to a {@link ConversationOptionResolverResult}.
     *
     * @return a {@link ConversationOptionResolverResult}
     * @throws InstructionParseException when the conversation could not be resolved
     */
    public ConversationOptionResolverResult resolve() throws InstructionParseException {
        final ConversationID conversationWithNextOption = new ConversationIDBuilder(pack, convName).build();

        //Since the conversation might be in another package we must load this again
        final ConversationData newData = plugin.getConversation(conversationWithNextOption);
        return new ConversationOptionResolverResult(newData, optionName);
    }

}
