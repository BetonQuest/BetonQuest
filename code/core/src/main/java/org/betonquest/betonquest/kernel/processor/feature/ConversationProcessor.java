package org.betonquest.betonquest.kernel.processor.feature;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.ConversationPublicData;
import org.betonquest.betonquest.conversation.DefaultConversationData;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.processor.PostLoadTask;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.lib.profile.ProfileKeyMap;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores Conversation Data and validates it.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ConversationProcessor extends SectionProcessor<ConversationIdentifier, DefaultConversationData> implements Conversations, PostLoadTask {

    /**
     * Factory to create class-specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The map of all active conversations.
     */
    private final Map<Profile, Conversation> activeConversations;

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
     * The config accessor.
     */
    private final ConfigAccessor configAccessor;

    /**
     * The quest package manager.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * The placeholder processor.
     */
    private final PlaceholderProcessor placeholderProcessor;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create a new Conversation Data Processor to load and process conversation data.
     *
     * @param log                  the custom logger for this class
     * @param loggerFactory        the logger factory to create new class specific logger
     * @param plugin               the plugin instance used for new conversation data
     * @param textCreator          the text creator to parse text
     * @param placeholderProcessor the placeholder processor to resolve placeholders
     * @param questPackageManager  the quest package manager to load quest packages
     * @param profileProvider      the profile provider to access profiles
     * @param configAccessor       the config accessor
     * @param convIORegistry       the registry for available ConversationIOs
     * @param interceptorRegistry  the registry for available Interceptors
     * @param placeholders         the {@link PlaceholderManager} to create and resolve placeholders
     * @param localizations        the Localizations instance to use for ingame notifications
     * @param actionManager        the action manager
     * @param conditionManager     the condition manager
     * @param identifierFactory    the identifier factory to create {@link ConversationIdentifier}s for this type
     * @param identifiers          the identifiers registry
     * @param saver                the saver to save data
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ConversationProcessor(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                 final Plugin plugin, final ParsedSectionTextCreator textCreator,
                                 final QuestPackageManager questPackageManager, final PlaceholderProcessor placeholderProcessor,
                                 final ProfileProvider profileProvider, final ConfigAccessor configAccessor,
                                 final ConversationIORegistry convIORegistry, final InterceptorRegistry interceptorRegistry,
                                 final Instructions placeholders, final Localizations localizations,
                                 final ActionManager actionManager, final ConditionManager conditionManager,
                                 final IdentifierFactory<ConversationIdentifier> identifierFactory,
                                 final Identifiers identifiers, final Saver saver) {
        super(log, placeholders, identifierFactory, "Conversation", "conversations");
        this.loggerFactory = loggerFactory;
        this.activeConversations = new ProfileKeyMap<>(profileProvider, new ConcurrentHashMap<>());
        this.starter = new ConversationStarter(loggerFactory, loggerFactory.create(ConversationStarter.class),
                activeConversations, plugin, localizations, actionManager, conditionManager, this, identifiers, saver);
        this.textCreator = textCreator;
        this.questPackageManager = questPackageManager;
        this.placeholderProcessor = placeholderProcessor;
        this.convIORegistry = convIORegistry;
        this.interceptorRegistry = interceptorRegistry;
        this.configAccessor = configAccessor;
        this.conditionManager = conditionManager;
        this.listener = new ConversationListener(loggerFactory.create(ConversationListener.class), this, profileProvider,
                localizations, configAccessor);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void clear() {
        super.clear();
        listener.reload();
    }

    @Override
    protected Map.Entry<ConversationIdentifier, DefaultConversationData> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final ConfigurationSection section = instruction.getSection();
        final ConversationIdentifier identifier = getIdentifier(pack, sectionName);
        log.debug(pack, String.format("Loading conversation '%s'.", identifier));

        final boolean invincible = configAccessor.getBoolean("conversation.damage.invincible");
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

        final ConversationPublicData publicData = new ConversationPublicData(identifier, quester, stop, finalActions, conversationIO, interceptor, interceptorDelay, invincible);
        final DefaultConversationData conversationData = new DefaultConversationData(loggerFactory.create(DefaultConversationData.class), questPackageManager,
                placeholderProcessor, conditionManager, instruction, this, textCreator, section, publicData);
        return Map.entry(identifier, conversationData);
    }

    private String defaultingValue(final ConfigurationSection section, final String path, final String configPath, final String defaultConfig) {
        if (section.isSet(path)) {
            return Objects.requireNonNull(section.getString(path));
        }
        return configAccessor.getString(configPath, defaultConfig);
    }

    /**
     * Validates all pointers to conversations and removes them when the target conversation is not loaded.
     * <p>
     * This method should be invoked after loading QuestPackages.
     *
     * @see DefaultConversationData#checkExternalPointers()
     */
    @Override
    public void startAll() {
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

    /**
     * Gets the conversation data for the given identifier.
     *
     * @param conversationID the identifier of the conversation to get the data for
     * @return the conversation data
     * @throws QuestException if the conversation is not loaded.
     */
    public DefaultConversationData getData(final ConversationIdentifier conversationID) throws QuestException {
        return get(conversationID);
    }

    /**
     * Gets the map of all active conversations.
     *
     * @return the map of all active conversations.
     */
    public Map<Profile, Conversation> getActiveConversations() {
        return activeConversations;
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

    /**
     * Gets the active conversation for the given profile.
     *
     * @param profile the profile to get the active conversation for.
     * @return the active conversation or null if there is no active conversation.
     */
    @Nullable
    public Conversation getActiveConversation(final Profile profile) {
        return activeConversations.get(profile);
    }

    @Override
    public void cancel(final OnlineProfile profile) {
        final Conversation conversation = getActiveConversation(profile);
        if (conversation != null) {
            conversation.endConversation();
        }
    }

    @Override
    public Optional<ConversationIdentifier> getActive(final Profile profile) {
        return Optional.ofNullable(getActiveConversation(profile)).map(Conversation::getID);
    }

    @Override
    public void sendBypassMessage(final OnlineProfile profile, final Component message) {
        final Conversation activeConversation = getActiveConversation(profile);
        if (activeConversation == null) {
            profile.getPlayer().sendMessage(message);
        } else {
            activeConversation.sendMessage(message);
        }
    }

    @Override
    public Optional<Component> getActiveQuesterName(final Profile profile) {
        final Conversation activeConversation = getActiveConversation(profile);
        if (activeConversation == null) {
            return Optional.empty();
        }
        final Text quester = activeConversation.getData().getPublicData().quester();
        try {
            return Optional.of(quester.asComponent(profile));
        } catch (final QuestException e) {
            log.debug("Could not resolve quester name for profile '%s' in conversation '%s': %s".formatted(profile, activeConversation.getID(), e.getMessage()), e);
            return Optional.empty();
        }
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
