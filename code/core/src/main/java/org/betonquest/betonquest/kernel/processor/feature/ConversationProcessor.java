package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.BooleanParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.instruction.argument.parser.StringParser;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.conversation.ConversationID;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores Conversation Data and validates it.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ConversationProcessor extends SectionProcessor<ConversationID, ConversationData> implements ConversationApi {

    /**
     * Factory to create class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The map of all active conversations.
     */
    private final Map<Profile, Conversation> activeConversations;

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
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Starter for conversations.
     */
    private final ConversationStarter starter;

    /**
     * Listener for interactions in a conversation.
     */
    private final ConversationListener listener;

    /**
     * Create a new Conversation Data Processor to load and process conversation data.
     *
     * @param log                 the custom logger for this class
     * @param loggerFactory       the logger factory to create new class specific logger
     * @param plugin              the plugin instance used for new conversation data
     * @param textCreator         the text creator to parse text
     * @param convIORegistry      the registry for available ConversationIOs
     * @param interceptorRegistry the registry for available Interceptors
     * @param variables           the variable processor to create and resolve variables
     * @param pluginMessage       the plugin message instance to use for ingame notifications
     */
    public ConversationProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                 final BetonQuest plugin, final ParsedSectionTextCreator textCreator,
                                 final ConversationIORegistry convIORegistry, final InterceptorRegistry interceptorRegistry,
                                 final Variables variables, final PluginMessage pluginMessage) {
        super(log, variables, plugin.getQuestPackageManager(), "Conversation", "conversations");
        this.loggerFactory = loggerFactory;
        this.activeConversations = new ProfileKeyMap<>(plugin.getProfileProvider(), new ConcurrentHashMap<>());
        this.starter = new ConversationStarter(loggerFactory, loggerFactory.create(ConversationStarter.class),
                activeConversations, plugin, pluginMessage);
        this.plugin = plugin;
        this.textCreator = textCreator;
        this.convIORegistry = convIORegistry;
        this.interceptorRegistry = interceptorRegistry;
        this.listener = new ConversationListener(loggerFactory.create(ConversationListener.class), this, plugin.getProfileProvider(),
                pluginMessage, plugin.getPluginConfig());
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void clear() {
        super.clear();
        listener.reload();
    }

    @Override
    protected ConversationData loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final ConversationID conversationID = getIdentifier(pack, section.getName());
        log.debug(pack, String.format("Loading conversation '%s'.", conversationID));

        final Text quester = textCreator.parseFromSection(pack, section, "quester");
        final CreationHelper helper = new CreationHelper(pack, section);
        final Variable<Boolean> blockMovement = new DefaultVariable<>(variables, pack, section.getString("stop", "false"), new BooleanParser());
        final Variable<ConversationIOFactory> convIO = helper.parseConvIO();
        final Variable<InterceptorFactory> interceptor = helper.parseInterceptor();
        final Variable<Number> interceptorDelay = helper.parseInterceptorDelay();
        final Variable<List<EventID>> finalEvents = new VariableList<>(variables, pack, section.getString("final_events", ""), value -> new EventID(variables, packManager, pack, value));
        final boolean invincible = plugin.getConfig().getBoolean("conversation.damage.invincible");
        final ConversationData.PublicData publicData = new ConversationData.PublicData(conversationID, quester, blockMovement, finalEvents, convIO, interceptor, interceptorDelay, invincible);

        return new ConversationData(loggerFactory.create(ConversationData.class), packManager,
                variables, plugin.getQuestTypeApi(), plugin.getFeatureApi().conversationApi(), textCreator, section, publicData);
    }

    @Override
    protected ConversationID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ConversationID(packManager, pack, identifier);
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
                log.warn(convData.getPack(), "Error in '" + convData.getPublicData().conversationID()
                        + "' conversation: " + e.getMessage(), e);
                return true;
            }
            return false;
        });
    }

    @Override
    public ConversationData getData(final ConversationID conversationID) throws QuestException {
        return get(conversationID);
    }

    @Override
    public void start(final OnlineProfile onlineProfile, final ConversationID conversationID, final Location center, @Nullable final String startingOption) {
        starter.startConversation(onlineProfile, conversationID, center, startingOption);
    }

    @Override
    public boolean hasActive(final Profile profile) {
        return activeConversations.containsKey(profile);
    }

    @Override
    @Nullable
    public Conversation getActive(final Profile profile) {
        return activeConversations.get(profile);
    }

    /**
     * Gets the object that actually starts conversations.
     *
     * @return the conversation starter
     */
    public ConversationStarter getStarter() {
        return starter;
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

        /**
         * The string parser to use for parsing strings.
         */
        private final StringParser stringParser;

        /**
         * The number parser to use for parsing numbers.
         */
        private final DecoratedArgumentParser<Number> numberParser;

        private CreationHelper(final QuestPackage pack, final ConfigurationSection section) {
            this.pack = pack;
            this.section = section;
            this.stringParser = new StringParser();
            this.numberParser = new DecoratableArgumentParser<>(NumberParser.DEFAULT)
                    .validate(value -> value.doubleValue() > 0,
                            "Expected a non-negative number for 'interceptor_delay', got '%s' instead.");
        }

        @Nullable
        private String opt(final String path) {
            return section.getString(path);
        }

        private String defaulting(final String path, final String configPath, final String defaultConfig) {
            if (section.isSet(path)) {
                return Objects.requireNonNull(opt(path));
            }
            return plugin.getPluginConfig().getString(configPath, defaultConfig);
        }

        private Variable<ConversationIOFactory> parseConvIO() throws QuestException {
            final String rawConvIOs = defaulting("conversationIO", "conversation.default_io", "menu,tellraw");
            return new DefaultVariable<>(variables, pack, rawConvIOs, value -> {
                final List<String> ios = new VariableList<>(variables, pack, value, stringParser).getValue(null);
                return convIORegistry.getFactory(ios);
            });
        }

        private Variable<InterceptorFactory> parseInterceptor() throws QuestException {
            final String rawInterceptor = defaulting("interceptor", "conversation.interceptor.default", "simple");
            return new DefaultVariable<>(variables, pack, rawInterceptor, value -> {
                final List<String> interceptors = new VariableList<>(variables, pack, value, stringParser).getValue(null);
                return interceptorRegistry.getFactory(interceptors);
            });
        }

        private Variable<Number> parseInterceptorDelay() throws QuestException {
            final String rawInterceptorDelay = defaulting("interceptor_delay", "conversation.interceptor.delay", "50");
            return new DefaultVariable<>(variables, pack, rawInterceptorDelay, value -> numberParser.apply(variables, packManager, pack, value));
        }
    }
}
