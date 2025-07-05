package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable.UnmodifiableConfigurationSection;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessageCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.betonquest.betonquest.conversation.ConversationData.OptionType.NPC;
import static org.betonquest.betonquest.conversation.ConversationData.OptionType.PLAYER;

/**
 * Represents the data of the conversation.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.TooManyMethods"})
public class ConversationData {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * All references made by this conversation's pointers to other conversations.
     */
    private final List<CrossConversationReference> externalPointers = new ArrayList<>();

    /**
     * The {@link VariableProcessor} to resolve variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Message creator to parse messages.
     */
    private final ParsedSectionMessageCreator messageCreator;

    /**
     * The {@link QuestPackage} this conversation is in.
     */
    private final QuestPackage pack;

    /**
     * The name of this conversation.
     */
    private final String convName;

    /**
     * The external used data.
     */
    private final PublicData publicData;

    /**
     * The NPC options that the conversation can start from.
     */
    private final List<String> startingOptions;

    /**
     * A map of all things the NPC can say during this conversation.
     * The key is the option name that can be pointed to.
     */
    private final Map<String, ConversationOption> npcOptions;

    /**
     * A map of all things the player can say during this conversation.
     * The key is the option name that can be pointed to.
     */
    private final Map<String, ConversationOption> playerOptions;

    /**
     * Loads conversation from package.
     *
     * @param log            the custom logger for this class
     * @param questTypeAPI   the quest type api
     * @param featureAPI     the feature api
     * @param messageCreator the message creator to parse messages
     * @param pack           the package of the conversation this data represents
     * @param convSection    the configuration section of the conversation
     * @param publicData     the external used data
     * @throws QuestException when there is a syntax error in the defined conversation or
     *                        when conversation options cannot be resolved or {@code convSection} is null
     */
    public ConversationData(final BetonQuestLogger log, final VariableProcessor variableProcessor, final QuestTypeAPI questTypeAPI, final FeatureAPI featureAPI,
                            final ParsedSectionMessageCreator messageCreator, final QuestPackage pack,
                            final ConfigurationSection convSection, final PublicData publicData) throws QuestException {
        this.log = log;
        this.variableProcessor = variableProcessor;
        this.questTypeAPI = questTypeAPI;
        this.featureAPI = featureAPI;
        this.pack = pack;
        this.convName = publicData.convName();
        this.publicData = publicData;
        this.messageCreator = messageCreator;

        this.npcOptions = loadNpcOptions(convSection);
        this.startingOptions = loadStartingOptions(convSection);
        this.playerOptions = loadPlayerOptions(convSection);
        validateNpcOptions();
        validatePlayerOptions();

        log.debug(pack, String.format("Conversation loaded: %d NPC options and %d player options", npcOptions.size(),
                playerOptions.size()));
    }

    /**
     * Checks if external pointers point to valid options. This cannot be checked
     * when constructing {@link ConversationData} objects because conversations that are
     * being pointed to may not yet exist.
     * <p>
     * This method should be called when all conversations are loaded.
     *
     * @throws QuestException when a pointer to an external conversation could not be resolved
     */
    public void checkExternalPointers() throws QuestException {
        for (final CrossConversationReference externalPointer : externalPointers) {

            final ResolvedOption resolvedPointer = externalPointer.resolver().resolve();
            final QuestPackage targetPack = resolvedPointer.conversationData().pack;
            final String targetConvName = resolvedPointer.conversationData().convName;
            final String targetOptionName = resolvedPointer.name();

            // This is null if we refer to the starting options of a conversation
            if (targetOptionName == null) {
                continue;
            }

            final String sourceOption;
            if (externalPointer.sourceOption() == null) {
                sourceOption = "starting option";
            } else {
                sourceOption = "'" + externalPointer.sourceOption() + "' option";
            }

            final ConversationData conv;
            try {
                conv = featureAPI.getConversation(new ConversationID(targetPack, targetConvName));
            } catch (final QuestException e) {
                log.warn("Cross-conversation pointer in '" + externalPointer.sourcePack() + "' package, '" + externalPointer.sourceConv() + "' conversation, "
                        + sourceOption + " points to the '" + targetConvName
                        + "' conversation in the package '" + targetPack.getQuestPath() + "' but that conversation does not exist. Check your spelling!", e);
                continue;
            }

            if (!conv.hasOption(resolvedPointer)) {
                log.warn(conv.pack, "External pointer in '" + externalPointer.sourcePack() + "' package, '" + externalPointer.sourceConv() + "' conversation, "
                        + sourceOption + " points to '" + targetOptionName + "' NPC option in '" + targetConvName
                        + "' conversation from package '" + targetPack.getQuestPath() + "', but it does not exist.");
            }
        }
        externalPointers.clear();
    }

    /**
     * Resolves a pointer to an option in a conversation.
     *
     * @param pack                    the package from which we are searching for the conversation
     * @param currentConversationName the current conversation data's name
     * @param currentOptionName       the option string to resolve
     * @param optionType              the {@link ConversationData.OptionType} of the option
     * @return a {@link CrossConversationReference} pointing to the option
     * @throws QuestException when the conversation could not be resolved
     */
    private CrossConversationReference resolvePointer(final QuestPackage pack, final String currentConversationName,
                                                      @Nullable final String currentOptionName, final OptionType optionType,
                                                      final String option) throws QuestException {
        final ConversationOptionResolver resolver = new ConversationOptionResolver(featureAPI, pack, currentConversationName, optionType, option);
        return new CrossConversationReference(pack, currentConversationName, currentOptionName, resolver);
    }

    private void validatePlayerOptions() throws QuestException {
        for (final ConversationOption option : playerOptions.values()) {
            for (final String pointer : option.getPointers(null)) {
                if (pointer.contains(".")) {
                    externalPointers.add(resolvePointer(pack, convName, option.getName(), NPC, pointer));
                } else if (!npcOptions.containsKey(pointer)) {
                    throw new QuestException(
                            String.format("Player option %s points to %s NPC option, but it does not exist",
                                    option.getName(), pointer));
                }
            }
            validateExtends(pack, option, PLAYER);
        }
    }

    private void validateExtends(final QuestPackage pack, final ConversationOption option, final OptionType optionType) throws QuestException {
        final Map<String, ConversationData.ConversationOption> optionMap;
        if (optionType == PLAYER) {
            optionMap = playerOptions;
        } else {
            optionMap = npcOptions;
        }
        for (final String extend : option.getExtends()) {
            if (extend.contains(".")) {
                externalPointers.add(resolvePointer(pack, convName, option.getName(), optionType, extend));
            } else {
                if (!optionMap.containsKey(extend)) {
                    throw new QuestException(String.format("%s %s extends %s, but it does not exist",
                            optionType.readable, option.getName(), extend));
                }
            }
        }
    }

    private void validateNpcOptions() throws QuestException {
        for (final ConversationOption option : npcOptions.values()) {
            for (final String pointer : option.getPointers(null)) {
                if (pointer.contains(".")) {
                    externalPointers.add(resolvePointer(pack, convName, option.getName(), PLAYER, pointer));
                } else if (!playerOptions.containsKey(pointer)) {
                    throw new QuestException(
                            String.format("NPC option %s points to %s player option, but it does not exist",
                                    option.getName(), pointer));
                }
            }
            validateExtends(pack, option, NPC);
        }
    }

    /**
     * Checks if all starting options point to existing NPC options.
     *
     * @return the loaded starting options
     * @throws QuestException when the conversation could not be resolved
     */
    private List<String> loadStartingOptions(final ConfigurationSection convSection) throws QuestException {
        final List<String> startingOptions;
        try {
            startingOptions = new VariableList<>(variableProcessor, pack, convSection.getString("first", ""),
                    Argument.STRING, VariableList.notEmptyChecker()).getValue(null);
        } catch (final QuestException e) {
            throw new QuestException("Could not load starting options: " + e.getMessage(), e);
        }

        for (final String startingOption : startingOptions) {
            if (startingOption.contains(".")) {
                externalPointers.add(resolvePointer(pack, convName, null, NPC, startingOption));
            } else if (!npcOptions.containsKey(startingOption)) {
                throw new QuestException("Starting option " + startingOption + " does not exist");
            }
        }
        return startingOptions;
    }

    private Map<String, ConversationOption> loadPlayerOptions(final ConfigurationSection conv) throws QuestException {
        final ConfigurationSection playerSection = conv.getConfigurationSection("player_options");
        final Map<String, ConversationOption> playerOptions = new HashMap<>();
        if (playerSection != null) {
            for (final String name : playerSection.getKeys(false)) {
                playerOptions.put(name, new ConversationOption(name, PLAYER, conv));
            }
        }
        return playerOptions;
    }

    private Map<String, ConversationOption> loadNpcOptions(final ConfigurationSection convSection) throws QuestException {
        final ConfigurationSection npcSection = convSection.getConfigurationSection("NPC_options");
        if (npcSection == null) {
            throw new QuestException("NPC_options section not defined");
        }
        final Map<String, ConversationOption> npcOptions = new HashMap<>();
        for (final String name : npcSection.getKeys(false)) {
            npcOptions.put(name, new ConversationOption(name, NPC, convSection));
        }
        return npcOptions;
    }

    /**
     * Returns all addresses of options that are available after the provided option is selected.
     *
     * @param profile the profile of the player to get the pointers for
     * @param option  the option to get the pointers for
     * @return a list of pointer addresses
     */
    @SuppressWarnings("NullAway")
    public List<String> getPointers(final Profile profile, final ResolvedOption option) {
        final Map<String, ConversationOption> optionMaps;
        if (option.type() == NPC) {
            optionMaps = option.conversationData().npcOptions;
        } else {
            optionMaps = option.conversationData().playerOptions;
        }
        return optionMaps.get(option.name()).getPointers(profile);
    }

    /**
     * Get the public data.
     *
     * @return external used data
     */
    public PublicData getPublicData() {
        return publicData;
    }

    /**
     * Returns a list of all option names that the conversation can start from.
     *
     * @return a list of all option names
     */
    public List<String> getStartingOptions() {
        return new ArrayList<>(startingOptions);
    }

    /**
     * Checks if the option exists.
     *
     * @param option the option toc check for existence
     * @return the existence of the option
     */
    private boolean hasOption(final ResolvedOption option) {
        final ConversationOption opt;
        if (option.type() == NPC) {
            opt = option.conversationData().npcOptions.get(option.name());
        } else {
            opt = option.conversationData().playerOptions.get(option.name());
        }
        return opt != null;
    }

    /**
     * Gets the text of the specified option in the specified language.
     * Respects extended options.
     *
     * @param profile the profile of the player
     * @param option  the option
     * @return the text of the specified option in the specified language
     */
    @Nullable
    public Component getText(@Nullable final Profile profile, final ResolvedOption option) {
        final ConversationOption opt;
        if (option.type() == NPC) {
            opt = option.conversationData().npcOptions.get(option.name());
        } else {
            opt = option.conversationData().playerOptions.get(option.name());
        }
        if (opt == null) {
            return null;
        }
        return opt.getText(profile);
    }

    /**
     * Gets the properties of the specified option.
     * This is a section that can contain any properties defined by the conversation.
     *
     * @param profile the profile of the player
     * @param option  the option
     * @return the properties of the specified option
     */
    public ConfigurationSection getProperties(@Nullable final Profile profile, final ResolvedOption option) {
        final ConversationOption opt;
        if (option.type() == NPC) {
            opt = option.conversationData().npcOptions.get(option.name());
        } else {
            opt = option.conversationData().playerOptions.get(option.name());
        }
        if (opt == null) {
            return new UnmodifiableConfigurationSection(new MemoryConfiguration());
        }
        return opt.getProperties(profile);
    }

    /**
     * Gets the package containing this conversation.
     *
     * @return the package containing this conversation
     */
    public QuestPackage getPack() {
        return pack;
    }

    /**
     * Gets the conditions required for the specified option to be selected.
     *
     * @param option the conversation option
     * @param type   the type of the option
     * @return the conditions required for the specified option to be selected
     */
    @SuppressWarnings("NullAway")
    public List<ConditionID> getConditionIDs(final String option, final OptionType type) {
        final Map<String, ConversationOption> options = type == NPC ? npcOptions : playerOptions;
        return options.get(option).getConditions();
    }

    /**
     * Gets the events that will be executed when the specified option is selected.
     *
     * @param profile the profile of the player
     * @param option  the name of the conversation option
     * @param type    the type of the option
     * @return a list of {@link EventID}s
     */
    public List<EventID> getEventIDs(final Profile profile, final ResolvedOption option, final OptionType type) {
        final Map<String, ConversationOption> options;
        if (type == NPC) {
            options = option.conversationData().npcOptions;
        } else {
            options = option.conversationData().playerOptions;
        }
        if (options.containsKey(option.name())) {
            return options.get(option.name()).getEvents(profile);
        } else {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("NullAway")
    private ConversationOption getOption(@Nullable final String option, final OptionType type) {
        return type == NPC ? npcOptions.get(option) : playerOptions.get(option);
    }

    /**
     * Checks if the conversation can start for the given player. This means it must have at least one option with
     * conditions that are met by the player.
     *
     * @param profile the {@link Profile} of the player
     * @return True, if the player can star the conversation.
     * @throws QuestException if an external pointer reference has an invalid format or
     *                        if an external pointer inside the conversation could not be resolved
     */
    @SuppressWarnings("NullAway")
    public boolean isReady(final Profile profile) throws QuestException {
        for (final String option : getStartingOptions()) {
            final ConversationData sourceData;
            final String optionName;
            if (option.contains(".")) {
                final ResolvedOption result = new ConversationOptionResolver(featureAPI, pack, this.convName, NPC, option).resolve();
                sourceData = result.conversationData();
                optionName = result.name();
            } else {
                sourceData = this;
                optionName = option;
            }
            if (questTypeAPI.conditions(profile, sourceData.getConditionIDs(optionName, NPC))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Types of conversation options.
     */
    public enum OptionType {

        /**
         * Things the NPC says.
         */
        NPC("NPC_options", "NPC option"),

        /**
         * Options selectable by the player.
         */
        PLAYER("player_options", "player option");

        /**
         * The section name.
         */
        private final String identifier;

        /**
         * The name to use in logging.
         */
        private final String readable;

        OptionType(final String identifier, final String readable) {
            this.identifier = identifier;
            this.readable = readable;
        }

        /**
         * Get the section name.
         *
         * @return section identifier
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * Gets the readable type name.
         *
         * @return name to use in log messages
         */
        public String getReadable() {
            return readable;
        }
    }

    /**
     * The external used data.
     *
     * @param convName      The name of this conversation.
     * @param quester       A map of the quester's name in different languages.
     * @param blockMovement If true, the player will not be able to move during this conversation.
     * @param finalEvents   All events that will be executed when the conversation ends.
     * @param convIO        The conversation IO that should be used for this conversation.
     * @param interceptor   The interceptor that should be used for this conversation.
     */
    public record PublicData(String convName, Message quester, Variable<Boolean> blockMovement,
                             Variable<List<EventID>> finalEvents, Variable<ConversationIOFactory> convIO,
                             Variable<InterceptorFactory> interceptor, boolean invincible) {

        /**
         * Gets the quester's name in the specified language.
         * If the name is not translated the default language will be used.
         * <p>
         * Returns "Quester" in case of an exception.
         *
         * @param log     the logger used when the name could not be resolved
         * @param profile the profile to resolve the variable
         * @return the quester's name in the specified language
         */
        public Component getQuester(final BetonQuestLogger log, final Profile profile) {
            try {
                return quester.asComponent(profile);
            } catch (final QuestException e) {
                log.warn("Could not get Quester's name! Using 'Quester' instead, reason: " + e.getMessage(), e);
                return Component.text("Quester");
            }
        }
    }

    /**
     * Represents a conversation option.
     */
    private class ConversationOption {

        /**
         * The name of the option, as defined in the config.
         */
        private final String optionName;

        /**
         * The {@link OptionType} of the option.
         */
        private final OptionType type;

        /**
         * A map of the text of the option in different languages.
         */
        @Nullable
        private final Message text;

        /**
         * Conditions that must be met for the option to be available.
         */
        private final List<ConditionID> conditions;

        /**
         * Events that are triggered when the option is selected.
         */
        private final List<EventID> events;

        /**
         * Other options that are available after this option is selected.
         */
        private final List<String> pointers;

        /**
         * Other options that this option extends from.
         */
        private final List<String> extendLinks;

        /**
         * Properties of the option.
         * This is a section that can contain any properties defined by the conversation.
         */
        private final ConfigurationSection properties;

        /**
         * Creates a ConversationOption.
         *
         * @param name        the name of the option, as defined in the config
         * @param type        the {@link OptionType} of the option
         * @param convSection the {@link ConfigurationSection} of the option
         * @throws QuestException if the configuration is invalid
         */
        protected ConversationOption(final String name, final OptionType type, final ConfigurationSection convSection) throws QuestException {
            this.optionName = name;
            this.type = type;
            final ConfigurationSection conv = convSection.getConfigurationSection(type.getIdentifier() + "." + name);

            if (conv == null) {
                text = null;
                conditions = List.of();
                events = List.of();
                pointers = List.of();
                extendLinks = List.of();
                properties = new UnmodifiableConfigurationSection(new MemoryConfiguration());
                return;
            }

            this.text = parseText(conv);
            this.conditions = resolve(conv, "conditions", ConditionID::new);
            this.events = resolve(conv, "events", EventID::new);

            pointers = resolve(conv, "pointers", Argument.STRING).stream()
                    .filter(StringUtils::isNotEmpty)
                    .toList();

            extendLinks = resolve(conv, "extends", Argument.STRING).stream()
                    .filter(StringUtils::isNotEmpty)
                    .toList();

            properties = new UnmodifiableConfigurationSection(Objects.requireNonNullElseGet(
                    conv.getConfigurationSection("properties"), MemoryConfiguration::new));
        }

        private <T> List<T> resolve(final ConfigurationSection conv, final String identifier,
                                    final Argument<T> resolver) throws QuestException {
            return new VariableList<>(variableProcessor, pack, conv.getString(identifier, ""), resolver).getValue(null);
        }

        private <T> List<T> resolve(final ConfigurationSection conv, final String identifier,
                                    final PackageArgument<T> resolver) throws QuestException {
            return resolve(conv, identifier, (value) -> resolver.apply(pack, value));
        }

        @Nullable
        private Message parseText(final ConfigurationSection conv) throws QuestException {
            if (!conv.contains("text")) {
                return null;
            }
            final Message text;
            try {
                text = messageCreator.parseFromSection(pack, conv, "text");
            } catch (final QuestException e) {
                throw new QuestException("Could not load text for " + optionName + " " + type.getReadable() + ": " + e.getMessage(), e);
            }
            return text;
        }

        /**
         * Returns the name of this option as it is defined in the config.
         *
         * @return the name of this option
         */
        public String getName() {
            return optionName;
        }

        /**
         * Returns the text of this option in the given language.
         *
         * @param profile the profile of the player to get the text for
         * @return the text of this option in the given language
         */
        public Component getText(@Nullable final Profile profile) {
            return getText(profile, new ArrayList<>());
        }

        private Component getText(@Nullable final Profile profile, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return Component.empty();
            }
            optionPath.add(getName());

            Component text = getFormattedText(profile);

            if (profile != null) {
                for (final String extend : extendLinks) {
                    if (questTypeAPI.conditions(profile, getOption(extend, type).getConditions())) {
                        text = text.append(getOption(extend, type).getText(profile, optionPath));
                        break;
                    }
                }
            }

            return text;
        }

        private Component getFormattedText(@Nullable final Profile profile) {
            if (text == null) {
                log.warn(pack, "No text in conversation '" + convName + "'!");
                return Component.empty();
            }
            try {
                return text.asComponent(profile);
            } catch (final QuestException e) {
                log.warn(pack, "Could not resolve message in conversation '" + convName + "': " + e.getMessage(), e);
                return Component.empty();
            }
        }

        /**
         * Returns all conditions that must be met for this option to be available.
         *
         * @return a list of {@link ConditionID}s
         */
        public List<ConditionID> getConditions() {
            return new ArrayList<>(conditions);
        }

        /**
         * Returns all events that are triggered when this option is selected.
         * This will also include events from extended options (if the conditions for these are true for the
         * given {@link Profile}).
         *
         * @param profile the profile of the player to get the events for
         * @return a list of {@link EventID}s
         */
        public List<EventID> getEvents(final Profile profile) {
            return getEvents(profile, new ArrayList<>());
        }

        private List<EventID> getEvents(final Profile profile, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return Collections.emptyList();
            }
            optionPath.add(getName());

            final List<EventID> events = new ArrayList<>(this.events);

            for (final String extend : extendLinks) {
                if (questTypeAPI.conditions(profile, getOption(extend, type).getConditions())) {
                    events.addAll(getOption(extend, type).getEvents(profile, optionPath));
                    break;
                }
            }
            return events;
        }

        /**
         * Returns all addresses of options that are available after this option is selected.
         * <br>
         * If the profile param is null pointers from extended options will not be included.
         *
         * @param profile the profile of the player to get the pointers for
         * @return a list of option addresses
         */
        public List<String> getPointers(@Nullable final Profile profile) {
            return getPointers(profile, new ArrayList<>());
        }

        private List<String> getPointers(@Nullable final Profile profile, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return Collections.emptyList();
            }
            optionPath.add(getName());

            final List<String> pointers = new ArrayList<>(this.pointers);

            if (profile != null) {
                for (final String extend : extendLinks) {
                    final ResolvedOption resolvedExtend;
                    try {
                        resolvedExtend = new ConversationOptionResolver(featureAPI, pack, convName, type, extend).resolve();
                    } catch (final QuestException e) {
                        log.reportException(pack, e);
                        throw new IllegalStateException("Cannot ensure a valid conversation flow with unresolvable pointers.", e);
                    }

                    final ConversationData targetConvData = resolvedExtend.conversationData();
                    if (questTypeAPI.conditions(profile, targetConvData.getOption(resolvedExtend.name(), type).getConditions())) {
                        pointers.addAll(targetConvData.getOption(resolvedExtend.name(), type).getPointers(profile, optionPath));
                        break;
                    }
                }
            }
            return pointers;
        }

        /**
         * Returns the names of all options this option extends from.
         *
         * @return a list of option names
         */
        public List<String> getExtends() {
            return new ArrayList<>(extendLinks);
        }

        /**
         * Returns the properties of this option.
         *
         * @return the properties of this option
         */
        public ConfigurationSection getProperties(@Nullable final Profile profile) {
            return getProperties(profile, new ArrayList<>());
        }

        private ConfigurationSection getProperties(@Nullable final Profile profile, final List<String> optionPath) {
            if (optionPath.contains(getName())) {
                return new MemoryConfiguration();
            }
            optionPath.add(getName());

            for (final String extend : extendLinks) {
                if (questTypeAPI.conditions(profile, getOption(extend, type).getConditions())) {
                    return new FallbackConfigurationSection(properties, getOption(extend, type).getProperties(profile, optionPath));
                }
            }
            return properties;
        }
    }
}
