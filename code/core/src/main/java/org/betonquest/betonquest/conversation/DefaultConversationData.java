package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable.UnmodifiableConfigurationSection;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.betonquest.betonquest.conversation.ConversationOptionType.NPC;
import static org.betonquest.betonquest.conversation.ConversationOptionType.PLAYER;

/**
 * Default implementation of {@link ConversationData}.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.TooManyMethods"})
public class DefaultConversationData implements ConversationData {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * All references made by this conversation's pointers to other conversations.
     */
    private final List<CrossConversationReference> externalPointers = new ArrayList<>();

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * The {@link SectionInstruction}.
     */
    private final SectionInstruction instruction;

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * The external used data.
     */
    private final ConversationPublicData publicData;

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
     * @param log             the custom logger for this class
     * @param packManager     the quest package manager to get quest packages from
     * @param placeholders    the {@link Placeholders} to create and resolve placeholders
     * @param questTypeApi    the quest type api
     * @param instruction     the instruction to parse the conversation from
     * @param conversationApi the Conversation API
     * @param textCreator     the text creator to parse text
     * @param convSection     the configuration section of the conversation
     * @param publicData      the external used data
     * @throws QuestException when there is a syntax error in the defined conversation or
     *                        when conversation options cannot be resolved or {@code convSection} is null
     */
    public DefaultConversationData(final BetonQuestLogger log, final QuestPackageManager packManager,
                                   final Placeholders placeholders, final QuestTypeApi questTypeApi,
                                   final SectionInstruction instruction,
                                   final ConversationApi conversationApi, final ParsedSectionTextCreator textCreator,
                                   final ConfigurationSection convSection, final ConversationPublicData publicData) throws QuestException {
        this.log = log;
        this.packManager = packManager;
        this.placeholders = placeholders;
        this.questTypeApi = questTypeApi;
        this.instruction = instruction;
        this.conversationApi = conversationApi;
        this.publicData = publicData;
        this.textCreator = textCreator;

        this.npcOptions = loadNpcOptions(convSection);
        this.startingOptions = loadStartingOptions();
        this.playerOptions = loadPlayerOptions(convSection);
        validateNpcOptions();
        validatePlayerOptions();

        log.debug(getPack(), String.format("Conversation loaded: %d NPC options and %d player options", npcOptions.size(),
                playerOptions.size()));
    }

    @Override
    public void checkExternalPointers() throws QuestException {
        for (final CrossConversationReference externalPointer : externalPointers) {

            final ResolvedOption resolvedOption = resolveOption(externalPointer.resolver(), externalPointer.optionType());
            final QuestPackage targetPack = resolvedOption.conversationData().getPublicData().conversationID().getPackage();
            final ConversationIdentifier targetConv = resolvedOption.conversationData().getPublicData().conversationID();
            final String targetOptionName = resolvedOption.name();

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

            final DefaultConversationData conv;
            try {
                conv = conversationApi.getData(targetConv);
            } catch (final QuestException e) {
                log.warn(getPack(), "Cross-conversation pointer in '" + externalPointer.sourcePack() + "' package, '" + externalPointer.sourceConv() + "' conversation, "
                        + sourceOption + " points to the '" + targetConv.get()
                        + "' conversation in the package '" + targetPack.getQuestPath() + "' but that conversation does not exist. Check your spelling!", e);
                continue;
            }

            if (!conv.hasOption(resolvedOption)) {
                log.warn(getPack(), "External pointer in '" + externalPointer.sourcePack() + "' package, '" + externalPointer.sourceConv() + "' conversation, "
                        + sourceOption + " points to '" + targetOptionName + "' NPC option in '" + targetConv.get()
                        + "' conversation from package '" + targetPack.getQuestPath() + "', but it does not exist.");
            }
        }
        externalPointers.clear();
    }

    /**
     * Resolves a pointer to an option in a conversation.
     *
     * @param currentOptionName the option string to resolve
     * @param optionType        the {@link ConversationOptionType} of the option
     * @return a {@link CrossConversationReference} pointing to the option
     * @throws QuestException when the conversation could not be resolved
     */
    private CrossConversationReference resolvePointer(@Nullable final String currentOptionName,
                                                      final ConversationOptionType optionType, final String option) throws QuestException {
        final ConversationOptionIdentifier identifier = instruction.chainForArgument(option).identifier(ConversationOptionIdentifier.class).get().getValue(null);
        return new CrossConversationReference(getPack(), publicData.conversationID(), currentOptionName, optionType, identifier);
    }

    @Override
    public ResolvedOption resolveOption(final ConversationOptionIdentifier conversationOptionID, final ConversationOptionType optionType) throws QuestException {
        final String conversationName = conversationOptionID.getConversationName();
        final String optionName = conversationOptionID.getOptionName();
        final ConversationIdentifier targetConversationID = conversationName == null ? publicData.conversationID()
                : instruction.chainForArgument(conversationName).identifier(ConversationIdentifier.class).get().getValue(null);

        final DefaultConversationData newData = conversationApi.getData(targetConversationID);
        return new ResolvedOption(newData, optionType, optionName);
    }

    private void validatePlayerOptions() throws QuestException {
        for (final ConversationOption option : playerOptions.values()) {
            for (final String pointer : option.getPointers(null)) {
                if (pointer.contains(".")) {
                    externalPointers.add(resolvePointer(option.getName(), NPC, pointer));
                } else if (!npcOptions.containsKey(pointer)) {
                    throw new QuestException(
                            String.format("Player option %s points to %s NPC option, but it does not exist",
                                    option.getName(), pointer));
                }
            }
            validateExtends(option, PLAYER);
        }
    }

    private void validateExtends(final ConversationOption option, final ConversationOptionType optionType) throws QuestException {
        final Map<String, DefaultConversationData.ConversationOption> optionMap;
        if (optionType == PLAYER) {
            optionMap = playerOptions;
        } else {
            optionMap = npcOptions;
        }
        for (final String extend : option.getExtends()) {
            if (extend.contains(".")) {
                externalPointers.add(resolvePointer(option.getName(), optionType, extend));
            } else {
                if (!optionMap.containsKey(extend)) {
                    throw new QuestException(String.format("%s %s extends %s, but it does not exist",
                            optionType.getReadable(), option.getName(), extend));
                }
            }
        }
    }

    private void validateNpcOptions() throws QuestException {
        for (final ConversationOption option : npcOptions.values()) {
            for (final String pointer : option.getPointers(null)) {
                if (pointer.contains(".")) {
                    externalPointers.add(resolvePointer(option.getName(), PLAYER, pointer));
                } else if (!playerOptions.containsKey(pointer)) {
                    throw new QuestException(
                            String.format("NPC option %s points to %s player option, but it does not exist",
                                    option.getName(), pointer));
                }
            }
            validateExtends(option, NPC);
        }
    }

    /**
     * Checks if all starting options point to existing NPC options.
     *
     * @return the loaded starting options
     * @throws QuestException when the conversation could not be resolved
     */
    private List<String> loadStartingOptions() throws QuestException {
        final List<String> startingOptions;
        try {
            startingOptions = instruction.read().value("first").string().list().notEmpty().get().getValue(null);
        } catch (final QuestException e) {
            throw new QuestException("Could not load starting options: " + e.getMessage(), e);
        }

        for (final String startingOption : startingOptions) {
            if (startingOption.contains(".")) {
                externalPointers.add(resolvePointer(null, NPC, startingOption));
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
                playerOptions.put(name, new ConversationOption(name, PLAYER, conv, instruction.getParsers()));
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
            npcOptions.put(name, new ConversationOption(name, NPC, convSection, instruction.getParsers()));
        }
        return npcOptions;
    }

    @SuppressWarnings("NullAway")
    @Override
    public List<String> getPointers(final Profile profile, final ResolvedOption option) {
        final Map<String, ConversationOption> optionMaps;
        if (option.type() == NPC) {
            optionMaps = option.conversationData().npcOptions;
        } else {
            optionMaps = option.conversationData().playerOptions;
        }
        return optionMaps.get(option.name()).getPointers(profile);
    }

    @Override
    public ConversationPublicData getPublicData() {
        return publicData;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public final QuestPackage getPack() {
        return publicData.conversationID().getPackage();
    }

    @SuppressWarnings("NullAway")
    @Override
    public List<ConditionIdentifier> getConditionIDs(final String option, final ConversationOptionType type) {
        final Map<String, ConversationOption> options = type == NPC ? npcOptions : playerOptions;
        return options.get(option).getConditions();
    }

    @Override
    public List<ActionIdentifier> getActionIDs(final Profile profile, final ResolvedOption option, final ConversationOptionType type) {
        final Map<String, ConversationOption> options;
        if (type == NPC) {
            options = option.conversationData().npcOptions;
        } else {
            options = option.conversationData().playerOptions;
        }
        if (options.containsKey(option.name())) {
            return options.get(option.name()).getActions(profile);
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("NullAway")
    private ConversationOption getOption(@Nullable final String option, final ConversationOptionType type) {
        return type == NPC ? npcOptions.get(option) : playerOptions.get(option);
    }

    @SuppressWarnings("NullAway")
    @Override
    public boolean isReady(final Profile profile) throws QuestException {
        for (final String option : getStartingOptions()) {
            final ConversationData sourceData;
            final String optionName;
            if (option.contains(".")) {
                final ConversationOptionIdentifier identifier = instruction.chainForArgument(option)
                        .identifier(ConversationOptionIdentifier.class).get().getValue(null);
                final ResolvedOption result = resolveOption(identifier, NPC);
                sourceData = result.conversationData();
                optionName = result.name();
            } else {
                sourceData = this;
                optionName = option;
            }
            if (questTypeApi.conditions(profile, sourceData.getConditionIDs(optionName, NPC))) {
                return true;
            }
        }
        return false;
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
         * The {@link ConversationOptionType} of the option.
         */
        private final ConversationOptionType type;

        /**
         * A map of the text of the option in different languages.
         */
        @Nullable
        private final Text text;

        /**
         * Conditions that must be met for the option to be available.
         */
        private final List<ConditionIdentifier> conditions;

        /**
         * Actions that are triggered when the option is selected.
         */
        private final List<ActionIdentifier> actions;

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
         * @param type        the {@link ConversationOptionType} of the option
         * @param convSection the {@link ConfigurationSection} of the option
         * @throws QuestException if the configuration is invalid
         */
        protected ConversationOption(final String name, final ConversationOptionType type, final ConfigurationSection convSection, final ArgumentParsers parsers) throws QuestException {
            this.optionName = name;
            this.type = type;
            final ConfigurationSection conv = convSection.getConfigurationSection(type.getIdentifier() + "." + name);
            if (conv == null) {
                text = null;
                conditions = List.of();
                actions = List.of();
                pointers = List.of();
                extendLinks = List.of();
                properties = new UnmodifiableConfigurationSection(new MemoryConfiguration());
                return;
            }

            this.text = parseText(conv);
            this.conditions = resolve(conv, "conditions", parsers.forIdentifier(ConditionIdentifier.class));
            this.actions = resolve(conv, "actions", parsers.forIdentifier(ActionIdentifier.class));

            pointers = resolve(conv, "pointers", parsers.string()).stream()
                    .filter(StringUtils::isNotEmpty)
                    .toList();

            extendLinks = resolve(conv, "extends", parsers.string()).stream()
                    .filter(StringUtils::isNotEmpty)
                    .toList();

            properties = new UnmodifiableConfigurationSection(Objects.requireNonNullElseGet(
                    conv.getConfigurationSection("properties"), MemoryConfiguration::new));
        }

        private <T> List<T> resolve(final ConfigurationSection conv, final String identifier,
                                    final DecoratedArgumentParser<T> resolver) throws QuestException {
            return new DefaultArgument<>(placeholders, getPack(), conv.getString(identifier, ""),
                    value -> resolver.list().apply(placeholders, packManager, getPack(), value)).getValue(null);
        }

        @Nullable
        private Text parseText(final ConfigurationSection conv) throws QuestException {
            if (!conv.contains("text")) {
                return null;
            }
            final Text text;
            try {
                text = textCreator.parseFromSection(getPack(), conv, "text");
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
                    if (questTypeApi.conditions(profile, getOption(extend, type).getConditions())) {
                        text = text.append(getOption(extend, type).getText(profile, optionPath));
                        break;
                    }
                }
            }

            return text;
        }

        private Component getFormattedText(@Nullable final Profile profile) {
            if (text == null) {
                log.warn(getPack(), "No text in conversation '" + publicData.conversationID() + "'!");
                return Component.empty();
            }
            try {
                return text.asComponent(profile);
            } catch (final QuestException e) {
                log.warn(getPack(), "Could not resolve message in conversation '" + publicData.conversationID() + "': " + e.getMessage(), e);
                return Component.empty();
            }
        }

        /**
         * Returns all conditions that must be met for this option to be available.
         *
         * @return a list of {@link ConditionIdentifier}s
         */
        public List<ConditionIdentifier> getConditions() {
            return new ArrayList<>(conditions);
        }

        /**
         * Returns all actions that are triggered when this option is selected.
         * This will also include actions from extended options (if the conditions for these are true for the
         * given {@link Profile}).
         *
         * @param profile the profile of the player to get the actions for
         * @return a list of {@link ActionIdentifier}s
         */
        public List<ActionIdentifier> getActions(final Profile profile) {
            return getActions(profile, new ArrayList<>());
        }

        private List<ActionIdentifier> getActions(final Profile profile, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return Collections.emptyList();
            }
            optionPath.add(getName());

            final List<ActionIdentifier> actions = new ArrayList<>(this.actions);

            for (final String extend : extendLinks) {
                if (questTypeApi.conditions(profile, getOption(extend, type).getConditions())) {
                    actions.addAll(getOption(extend, type).getActions(profile, optionPath));
                    break;
                }
            }
            return actions;
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
                        final ConversationOptionIdentifier identifier = instruction.chainForArgument(extend)
                                .identifier(ConversationOptionIdentifier.class).get().getValue(null);
                        resolvedExtend = resolveOption(identifier, type);
                    } catch (final QuestException e) {
                        log.reportException(getPack(), e);
                        throw new IllegalStateException("Cannot ensure a valid conversation flow with unresolvable pointers.", e);
                    }

                    final DefaultConversationData targetConvData = resolvedExtend.conversationData();
                    if (questTypeApi.conditions(profile, targetConvData.getOption(resolvedExtend.name(), type).getConditions())) {
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
                if (questTypeApi.conditions(profile, getOption(extend, type).getConditions())) {
                    return new FallbackConfigurationSection(properties, getOption(extend, type).getProperties(profile, optionPath));
                }
            }
            return properties;
        }
    }
}
