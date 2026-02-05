package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.service.BetonQuestConversations;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores Conversation Data and validates it.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ConversationProcessor extends SectionProcessor<ConversationIdentifier, ConversationData> implements ConversationApi, BetonQuestConversations {

    /**
     * Factory to create class-specific logger.
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
     * @param placeholders        the {@link Placeholders} to create and resolve placeholders
     * @param pluginMessage       the plugin message instance to use for ingame notifications
     * @param identifierFactory   the identifier factory to create {@link ConversationIdentifier}s for this type
     */
    public ConversationProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                 final BetonQuest plugin, final ParsedSectionTextCreator textCreator,
                                 final ConversationIORegistry convIORegistry, final InterceptorRegistry interceptorRegistry,
                                 final InstructionApi placeholders, final PluginMessage pluginMessage,
                                 final IdentifierFactory<ConversationIdentifier> identifierFactory) {
        super(log, placeholders, identifierFactory, "Conversation", "conversations");

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
    protected Map.Entry<ConversationIdentifier, ConversationData> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final ConfigurationSection section = instruction.getSection();
        final ConversationIdentifier identifier = getIdentifier(pack, sectionName);
        log.debug(pack, String.format("Loading conversation '%s'.", identifier));

        final boolean invincible = plugin.getConfig().getBoolean("conversation.damage.invincible");
        final Text quester = textCreator.parseFromSection(pack, section, "quester");

        final String rawConvIO = defaultingValue(section, "conversationIO", "conversation.default_io", "menu,tellraw");
        final String rawInterceptor = defaultingValue(section, "interceptor", "conversation.interceptor.default", "simple");
        final String rawInterceptorDelay = defaultingValue(section, "interceptor_delay", "conversation.interceptor.delay", "50");

        final Argument<Boolean> stop = instruction.read().value("stop").bool().getOptional(false);
        final Argument<List<ActionIdentifier>> finalActions = instruction.read().value("final_actions").identifier(ActionIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<ConversationIOFactory> conversationIO = instruction.chainForArgument(rawConvIO).string().list().map(convIORegistry::getFactory).get();
        final Argument<InterceptorFactory> interceptor = instruction.chainForArgument(rawInterceptor).string().list().map(interceptorRegistry::getFactory).get();
        final Argument<Number> interceptorDelay = instruction.chainForArgument(rawInterceptorDelay).number()
                .validate(delay -> delay.doubleValue() > 0, "Expected a non-negative number for 'interceptor_delay', got '%s' instead.").get();

        final ConversationData.PublicData publicData = new ConversationData.PublicData(identifier, quester, stop, finalActions, conversationIO, interceptor, interceptorDelay, invincible);
        final ConversationData conversationData = new ConversationData(loggerFactory.create(ConversationData.class), plugin.getQuestPackageManager(),
                plugin.getQuestTypeApi().placeholders(), plugin.getQuestTypeApi(), instruction, plugin.getFeatureApi().conversationApi(), textCreator, section, publicData);
        return Map.entry(identifier, conversationData);
    }

    private String defaultingValue(final ConfigurationSection section, final String path, final String configPath, final String defaultConfig) {
        if (section.isSet(path)) {
            return Objects.requireNonNull(section.getString(path));
        }
        return plugin.getPluginConfig().getString(configPath, defaultConfig);
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
    public ConversationData getData(final ConversationIdentifier conversationID) throws QuestException {
        return get(conversationID);
    }

    @Override
    public boolean canStart(final Profile profile, final ConversationIdentifier conversationIdentifier) throws QuestException {
        return getData(conversationIdentifier).isReady(profile);
    }

    @Override
    public void start(final OnlineProfile onlineProfile, final ConversationIdentifier conversationID, final Location center, @Nullable final String startingOption) {
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
}
