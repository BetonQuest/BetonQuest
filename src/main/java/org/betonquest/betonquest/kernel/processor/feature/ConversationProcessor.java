package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Message parser to parse messages.
     */
    private final MessageParser messageParser;

    /**
     * Player data storage to get the player language.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Registry for available ConversationIOs.
     */
    private final ConversationIORegistry convIORegistry;

    /**
     * Registry for available Interceptors.
     */
    private final InterceptorRegistry interceptorRegistry;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Create a new Conversation Data Processor to load and process conversation data.
     *
     * @param log                 the custom logger for this class
     * @param loggerFactory       the logger factory to create new class specific logger
     * @param plugin              the plugin instance used for new conversation data
     * @param variableProcessor   the processor to create new variables
     * @param messageParser       the message parser to parse messages
     * @param playerDataStorage   the player data storage to get the player language
     * @param convIORegistry      the registry for available ConversationIOs
     * @param interceptorRegistry the registry for available Interceptors
     * @param languageProvider    the language provider to get the default language
     */
    public ConversationProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                 final BetonQuest plugin, final VariableProcessor variableProcessor,
                                 final MessageParser messageParser, final PlayerDataStorage playerDataStorage,
                                 final ConversationIORegistry convIORegistry, final InterceptorRegistry interceptorRegistry,
                                 final LanguageProvider languageProvider) {
        super(log, "Conversation", "conversations");
        this.loggerFactory = loggerFactory;
        this.plugin = plugin;
        this.variableProcessor = variableProcessor;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
        this.convIORegistry = convIORegistry;
        this.interceptorRegistry = interceptorRegistry;
        this.languageProvider = languageProvider;
    }

    @Override
    protected ConversationData loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String convName = section.getName();
        log.debug(pack, String.format("Loading conversation '%s'.", convName));

        final ParsedSectionMessage quester = new ParsedSectionMessage(variableProcessor, messageParser, playerDataStorage, pack, section, "quester", languageProvider);
        final CreationHelper helper = new CreationHelper(pack, section);
        final boolean blockMovement = Boolean.parseBoolean(helper.opt("stop"));
        final String convIO = helper.parseConvIO();
        final String interceptor = helper.parseInterceptor();
        final List<EventID> finalEvents = helper.parseFinalEvents();
        final ConversationData.PublicData publicData = new ConversationData.PublicData(convName, quester, blockMovement, finalEvents, convIO, interceptor);

        return new ConversationData(loggerFactory.create(ConversationData.class), plugin.getQuestTypeAPI(), plugin.getFeatureAPI(),
                variableProcessor, messageParser, playerDataStorage, pack, section, publicData, languageProvider);
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
            return GlobalVariableResolver.resolve(pack, section.getString(path));
        }

        private String defaulting(final String path, final String configPath, final String defaultConfig) {
            if (section.isString(path)) {
                return Objects.requireNonNull(opt(path));
            }
            return plugin.getPluginConfig().getString(configPath, defaultConfig);
        }

        private String parseConvIO() throws QuestException {
            final String rawConvIOs = defaulting("conversationIO", "default_conversation_IO", "menu,tellraw");
            for (final String rawConvIOPart : rawConvIOs.split(",")) {
                final String rawConvIO = rawConvIOPart.trim();
                if (convIORegistry.getFactory(rawConvIO) != null) {
                    return rawConvIO;
                } else {
                    log.debug(pack, "Conversation IO '" + rawConvIO + "' not found. Trying next one...");
                }
            }
            throw new QuestException("No registered conversation IO found: " + rawConvIOs);
        }

        private String parseInterceptor() throws QuestException {
            final String rawInterceptor = defaulting("interceptor", "default_interceptor", "simple");
            for (final String s : rawInterceptor.split(",")) {
                final String trimmed = s.trim();
                if (interceptorRegistry.getFactory(trimmed) != null) {
                    return trimmed;
                }
            }
            throw new QuestException("No registered interceptor found: " + rawInterceptor);
        }

        private List<EventID> parseFinalEvents() throws QuestException {
            final String rawFinalEvents = opt("final_events");
            if (rawFinalEvents == null || rawFinalEvents.isEmpty()) {
                return new ArrayList<>(0);
            }
            final String[] array = rawFinalEvents.split(",");
            final List<EventID> finalEvents = new ArrayList<>(array.length);
            for (final String identifier : array) {
                try {
                    finalEvents.add(new EventID(pack, identifier));
                } catch (final QuestException e) {
                    throw new QuestException("Error while loading final events: " + e.getMessage(), e);
                }
            }
            return finalEvents;
        }
    }
}
