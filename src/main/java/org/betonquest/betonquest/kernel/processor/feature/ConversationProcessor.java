package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Stores Conversation Data and validates it.
 */
public class ConversationProcessor extends SectionProcessor<ConversationID, ConversationData> {
    /**
     * Factory to create class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Plugin instance used for new Conversation Data.
     */
    private final BetonQuest plugin;

    /**
     * Registry for available ConversationIOs.
     */
    private final ConversationIORegistry convIORegistry;

    /**
     * Registry for available Interceptors.
     */
    private final InterceptorRegistry interceptorRegistry;

    /**
     * Message creator to parse messages.
     */
    private final ParsedSectionMessageCreator messageCreator;

    /**
     * Create a new Conversation Data Processor to load and process conversation data.
     *
     * @param log                 the custom logger for this class
     * @param loggerFactory       the logger factory to create new class specific logger
     * @param plugin              the plugin instance used for new conversation data
     * @param messageCreator      the message creator to parse messages
     * @param convIORegistry      the registry for available ConversationIOs
     * @param interceptorRegistry the registry for available Interceptors
     */
    public ConversationProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                 final BetonQuest plugin, final ParsedSectionMessageCreator messageCreator,
                                 final ConversationIORegistry convIORegistry, final InterceptorRegistry interceptorRegistry) {
        super(log, "Conversation", "conversations");
        this.loggerFactory = loggerFactory;
        this.plugin = plugin;
        this.messageCreator = messageCreator;
        this.convIORegistry = convIORegistry;
        this.interceptorRegistry = interceptorRegistry;
    }

    @Override
    protected ConversationData loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String convName = section.getName();
        log.debug(pack, String.format("Loading conversation '%s'.", convName));

        final Message quester = messageCreator.parseFromSection(pack, section, "quester");
        final CreationHelper helper = new CreationHelper(pack, section);
        final Variable<Boolean> blockMovement = new Variable<>(plugin.getVariableProcessor(), pack, section.getString("stop", "false"), Argument.BOOLEAN);
        final Variable<ConversationIOFactory> convIO = helper.parseConvIO();
        final Variable<InterceptorFactory> interceptor = helper.parseInterceptor();
        final Variable<List<EventID>> finalEvents = new VariableList<>(plugin.getVariableProcessor(), pack, section.getString("final_events", ""), value -> new EventID(pack, value));
        final ConversationData.PublicData publicData = new ConversationData.PublicData(convName, quester, blockMovement, finalEvents, convIO, interceptor);

        return new ConversationData(loggerFactory.create(ConversationData.class), plugin.getVariableProcessor(),
                plugin.getQuestTypeAPI(), plugin.getFeatureAPI(), messageCreator, pack, section, publicData);
    }

    @Override
    protected ConversationID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ConversationID(pack, identifier);
    }

    /**
     * Validates all pointers to conversations and removes them when the target conversation is not loaded.
     * <p>
     * This method should be invoked after loading QuestPackages.
     *
     * @see ConversationData#checkExternalPointers()
     */
    public void checkExternalPointers() {
        values.entrySet().removeIf(entry -> {
            final ConversationData convData = entry.getValue();
            try {
                convData.checkExternalPointers();
            } catch (final QuestException e) {
                log.warn(convData.getPack(), "Error in '" + convData.getPack().getQuestPath() + "."
                        + convData.getPublicData().convName() + "' conversation: " + e.getMessage(), e);
                return true;
            }
            return false;
        });
    }

    /**
     * Class to bundle objects required to create a ConversationData.
     */
    private final class CreationHelper {
        /**
         * The conversation pack.
         */
        private final QuestPackage pack;

        /**
         * The conversation specific section.
         */
        private final ConfigurationSection section;

        private CreationHelper(final QuestPackage pack, final ConfigurationSection section) {
            this.pack = pack;
            this.section = section;
        }

        @Nullable
        private String opt(final String path) {
            return section.getString(path);
        }

        private String defaulting(final String path, final String configPath, final String defaultConfig) {
            if (section.isString(path)) {
                return Objects.requireNonNull(opt(path));
            }
            return plugin.getPluginConfig().getString(configPath, defaultConfig);
        }

        private Variable<ConversationIOFactory> parseConvIO() throws QuestException {
            final String rawConvIOs = defaulting("conversationIO", "default_conversation_IO", "menu,tellraw");
            return new Variable<>(plugin.getVariableProcessor(), pack, rawConvIOs, value -> {
                final List<String> ios = new VariableList<>(plugin.getVariableProcessor(), pack, value, Argument.STRING).getValue(null);
                return convIORegistry.getFactory(ios);
            });
        }

        private Variable<InterceptorFactory> parseInterceptor() throws QuestException {
            final String rawInterceptor = defaulting("interceptor", "default_interceptor", "simple");
            return new Variable<>(plugin.getVariableProcessor(), pack, rawInterceptor, value -> {
                final List<String> interceptors = new VariableList<>(plugin.getVariableProcessor(), pack, value, Argument.STRING).getValue(null);
                return interceptorRegistry.getFactory(interceptors);
            });
        }
    }
}
