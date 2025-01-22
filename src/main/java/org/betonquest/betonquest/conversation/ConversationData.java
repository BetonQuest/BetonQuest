package org.betonquest.betonquest.conversation;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals", "NullAway"})
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
     * The {@link BetonQuest} instance.
     */
    private final BetonQuest plugin;

    /**
     * The {@link ConversationID} of the conversation holding this data.
     */
    private final ConversationID conversationID;

    /**
     * The {@link QuestPackage} this conversation is in.
     */
    private final QuestPackage pack;

    /**
     * The name of this conversation.
     */
    private final String convName;

    /**
     * A map of the questers name in different languages.
     */
    private final Map<String, String> quester = new HashMap<>();

    /**
     * A map of the global conversation prefix in different languages.
     */
    private final Map<String, String> prefix = new HashMap<>();

    /**
     * If true, the player will not be able to move during this conversation.
     */
    private final boolean blockMovement;

    /**
     * All events that will be executed once the conversation has ended.
     */
    private final List<EventID> finalEvents = new ArrayList<>();

    /**
     * The NPC options that the conversation can start from.
     */
    private List<String> startingOptions;

    /**
     * A map of all things the NPC can say during this conversation.
     * The key is the option name that can be pointed to.
     */
    private Map<String, ConversationOption> npcOptions;

    /**
     * A map of all things the player can say during this conversation.
     * The key is the option name that can be pointed to.
     */
    private Map<String, ConversationOption> playerOptions;

    /**
     * The conversation IO that should be used for this conversation.
     */
    private String convIO;

    /**
     * The interceptor that should be used for this conversation.
     */
    private String interceptor;

    /**
     * Loads conversation from package.
     *
     * @param plugin         the plugin instance
     * @param conversationID the {@link ConversationID} of the conversation holding this data
     * @param convSection    the configuration section of the conversation
     * @throws QuestException          when there is a syntax error in the defined conversation
     * @throws ObjectNotFoundException when conversation options cannot be resolved or {@code convSection} is null
     */
    @SuppressWarnings({"PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    public ConversationData(final BetonQuest plugin, final ConversationID conversationID, final ConfigurationSection convSection) throws QuestException, ObjectNotFoundException {
        this.plugin = plugin;
        this.conversationID = conversationID;
        this.pack = conversationID.getPackage();
        this.convName = conversationID.getBaseID();
        this.log = plugin.getLoggerFactory().create(ConversationData.class);
        log.debug(pack, String.format("Loading conversation '%s'.", convName));

        if (convSection.get("quester") == null) {
            throw new QuestException("The 'quester' name is missing in the conversation file!");
        }
        if (convSection.isConfigurationSection("quester")) {
            for (final String lang : convSection.getConfigurationSection("quester").getKeys(false)) {
                quester.put(lang, ChatColor.translateAlternateColorCodes('&', pack.getString("conversations." + convName + ".quester." + lang)));
            }
        } else {
            quester.put(Config.getLanguage(), ChatColor.translateAlternateColorCodes('&', pack.getString("conversations." + convName + ".quester")));
        }
        if (convSection.isConfigurationSection("prefix")) {
            for (final String lang : convSection.getConfigurationSection("prefix").getKeys(false)) {
                final String pref = pack.getString("conversations." + convName + ".prefix." + lang);
                if (pref != null && !pref.isEmpty()) {
                    prefix.put(lang, pref);
                }
            }
        } else {
            final String pref = pack.getString("conversations." + convName + ".prefix");
            if (pref != null && !pref.isEmpty()) {
                prefix.put(Config.getLanguage(), pref);
            }
        }
        final String stop = pack.getString("conversations." + convName + ".stop");
        blockMovement = Boolean.parseBoolean(stop);
        final String rawConvIOs = pack.getString("conversations." + convName + ".conversationIO", plugin.getPluginConfig().getString("default_conversation_IO", "menu,tellraw"));

        // check if all data is valid (or at least exist)
        for (final String rawConvIOPart : rawConvIOs.split(",")) {
            final String rawConvIO = rawConvIOPart.trim();
            if (plugin.getFeatureRegistries().conversationIO().getFactory(rawConvIO) != null) {
                convIO = rawConvIO;
                break;
            } else {
                log.debug(pack, "Conversation IO '" + rawConvIO + "' not found. Trying next one...");
            }
        }
        if (convIO == null) {
            throw new QuestException("No registered conversation IO found: " + rawConvIOs);
        }

        final String rawInterceptor = pack.getString("conversations." + convName + ".interceptor", plugin.getPluginConfig().getString("default_interceptor", "simple"));
        for (final String s : rawInterceptor.split(",")) {
            if (plugin.getFeatureRegistries().interceptor().getFactory(s.trim()) != null) {
                interceptor = s.trim();
                break;
            }
        }
        if (interceptor == null) {
            throw new QuestException("No registered interceptor found: " + rawInterceptor);
        }

        if (quester.isEmpty()) {
            throw new QuestException("Quester's name is not defined");
        }
        for (final String value : quester.values()) {
            if (value == null) {
                throw new QuestException("Quester's name is not defined");
            }
        }

        parseOptions(pack, convSection);

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
     * @throws ObjectNotFoundException when a pointer to an external conversation could not be resolved
     */
    @SuppressWarnings("PMD.ExceptionAsFlowControl")
    public void checkExternalPointers() throws ObjectNotFoundException {
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
                conv = plugin.getConversation(new ConversationID(targetPack, targetConvName));
                if (conv == null) {
                    throw new ObjectNotFoundException("No Conversation for conversationID '" + conversationID.getFullID() + "'! Check for errors on /bq reload!");
                }
            } catch (final ObjectNotFoundException e) {
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
    private CrossConversationReference resolvePointer(final QuestPackage pack, final String currentConversationName, @Nullable final String currentOptionName, final OptionType optionType, final String option) throws QuestException, ObjectNotFoundException {
        final ConversationOptionResolver resolver = new ConversationOptionResolver(plugin, pack, currentConversationName, optionType, option);
        return new CrossConversationReference(pack, currentConversationName, currentOptionName, resolver);
    }

    private void parseOptions(final QuestPackage pack, final ConfigurationSection convSection) throws QuestException, ObjectNotFoundException {
        final String rawFinalEvents = pack.getString("conversations." + convName + ".final_events");
        if (rawFinalEvents != null && !rawFinalEvents.isEmpty()) {
            final String[] array = rawFinalEvents.split(",");
            for (final String identifier : array) {
                try {
                    finalEvents.add(new EventID(pack, identifier));
                } catch (final ObjectNotFoundException e) {
                    throw new QuestException("Error while loading final events: " + e.getMessage(), e);
                }
            }
        }

        loadNpcOptions(convSection);
        loadStartingOptions(pack);

        loadPlayerOptions(convSection);
        validateNpcOptions();
        validatePlayerOptions(pack);
    }

    private void validatePlayerOptions(final QuestPackage pack) throws QuestException, ObjectNotFoundException {
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

    private void validateExtends(final QuestPackage pack, final ConversationOption option, final OptionType optionType) throws QuestException, ObjectNotFoundException {
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

    private void validateNpcOptions() throws QuestException, ObjectNotFoundException {
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
     * @param pack the package containing this conversation
     * @throws QuestException when the conversation could not be resolved
     */
    private void loadStartingOptions(final QuestPackage pack) throws QuestException, ObjectNotFoundException {
        final String rawStartingOptions = pack.getString("conversations." + convName + ".first");
        if (rawStartingOptions == null || rawStartingOptions.isEmpty()) {
            throw new QuestException("Starting options are not defined");
        }

        startingOptions = Arrays.stream(rawStartingOptions.split(",")).map(String::trim).toList();

        for (final String startingOption : startingOptions) {
            if (startingOption.contains(".")) {
                externalPointers.add(resolvePointer(pack, convName, null, NPC, startingOption));
            } else if (!npcOptions.containsKey(startingOption)) {
                throw new QuestException("Starting option " + startingOption + " does not exist");
            }
        }
    }

    private void loadPlayerOptions(final ConfigurationSection conv) throws QuestException {
        final ConfigurationSection playerSection = conv.getConfigurationSection("player_options");
        playerOptions = new HashMap<>();
        if (playerSection != null) {
            for (final String name : playerSection.getKeys(false)) {
                playerOptions.put(name, new ConversationOption(conversationID, name, PLAYER, conv));
            }
        }
    }

    private void loadNpcOptions(final ConfigurationSection convSection) throws QuestException {
        final ConfigurationSection npcSection = convSection.getConfigurationSection("NPC_options");
        if (npcSection == null) {
            throw new QuestException("NPC_options section not defined");
        }
        npcOptions = new HashMap<>();
        for (final String name : npcSection.getKeys(false)) {
            npcOptions.put(name, new ConversationOption(conversationID, name, NPC, convSection));
        }
    }

    /**
     * Gets the conversation name.
     *
     * @return the name of this conversation
     */
    public String getName() {
        return convName;
    }

    /**
     * Gets the prefix of the conversation. If provided NPC option does not
     * define one, the global one from the conversation is returned instead.
     *
     * @param lang   language of the prefix
     * @param option the quest starting npc option that defines the prefix of the
     *               conversation
     * @return the conversation prefix, or null if not defined
     */
    public String getPrefix(final String lang, @Nullable final ResolvedOption option) {
        // get prefix from an option
        if (option != null) {
            String pref = option.conversationData().npcOptions.get(option.name()).getInlinePrefix(lang);
            if (pref == null) {
                pref = option.conversationData().npcOptions.get(option.name()).getInlinePrefix(Config.getLanguage());
            }
            if (pref != null) {
                return pref;
            }
        }

        // otherwise return global prefix
        String global = prefix.get(lang);
        if (global == null) {
            global = prefix.get(Config.getLanguage());
        }
        return global;
    }

    /**
     * Gets the quester's name in the specified language.
     * If the name is not translated the default language will be used.
     *
     * @param lang language key
     * @return the quester's name in the specified language
     */
    public String getQuester(final String lang) {
        return quester.get(lang) != null ? quester.get(lang) : quester.get(Config.getLanguage());
    }

    /**
     * Returns all addresses of options that are available after the provided option is selected.
     *
     * @param profile the profile of the player to get the pointers for
     * @param option  the option to get the pointers for
     * @return a list of pointer addresses
     */
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
     * @return the final events
     */
    public List<EventID> getFinalEvents() {
        return new ArrayList<>(finalEvents);
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
     * @return true if movement should be blocked
     */
    public boolean isMovementBlocked() {
        return blockMovement;
    }

    /**
     * @return the conversationIO
     */
    public String getConversationIO() {
        return convIO;
    }

    /**
     * @return the Interceptor
     */
    public String getInterceptor() {
        return interceptor;
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
     * @param lang    the desired language of the text
     * @param option  the option
     * @return the text of the specified option in the specified language
     */
    @Nullable
    public String getText(@Nullable final Profile profile, final String lang, final ResolvedOption option) {
        final ConversationOption opt;
        if (option.type() == NPC) {
            opt = option.conversationData().npcOptions.get(option.name());
        } else {
            opt = option.conversationData().playerOptions.get(option.name());
        }
        if (opt == null) {
            return null;
        }
        return opt.getText(profile, lang);
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

    private ConversationOption getOption(@Nullable final String option, final OptionType type) {
        return type == NPC ? npcOptions.get(option) : playerOptions.get(option);
    }

    /**
     * Checks if the conversation can start for the given player. This means it must have at least one option with
     * conditions that are met by the player.
     *
     * @param profile the {@link Profile} of the player
     * @return True, if the player can star the conversation.
     * @throws QuestException          if an external pointer reference has an invalid format
     * @throws ObjectNotFoundException if an external pointer inside the conversation could not be resolved
     */
    public boolean isReady(final Profile profile) throws QuestException, ObjectNotFoundException {
        for (final String option : getStartingOptions()) {
            final ConversationData sourceData;
            final String optionName;
            if (option.contains(".")) {
                final ResolvedOption result = new ConversationOptionResolver(plugin, pack, this.convName, NPC, option).resolve();
                sourceData = result.conversationData();
                optionName = result.name();
            } else {
                sourceData = this;
                optionName = option;
            }
            if (BetonQuest.conditions(profile, sourceData.getConditionIDs(optionName, NPC))) {
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
     * Represents a conversation option.
     */
    @SuppressWarnings("PMD.GodClass")
    private class ConversationOption {

        /**
         * The {@link QuestPackage} in which the conversation this option belongs to is defined.
         */
        private final QuestPackage pack;

        /**
         * The name of the conversation this option belongs to.
         */
        private final String conversationName;

        /**
         * The name of the option, as defined in the config.
         */
        private final String optionName;

        /**
         * The {@link OptionType} of the option.
         */
        private final OptionType type;

        /**
         * The inline prefix of the option.
         */
        private final Map<String, String> inlinePrefix = new HashMap<>();

        /**
         * A map of the text of the option in different languages.
         */
        private final Map<String, VariableString> text = new HashMap<>();

        /**
         * Conditions that must be met for the option to be available.
         */
        private final List<ConditionID> conditions = new ArrayList<>();

        /**
         * Events that are triggered when the option is selected.
         */
        private final List<EventID> events = new ArrayList<>();

        /**
         * Other options that are available after this option is selected.
         */
        private final List<String> pointers;

        /**
         * Other options that this option extends from.
         */
        private final List<String> extendLinks;

        /**
         * Creates a ConversationOption.
         *
         * @param conversationID the {@link ConversationID} of the conversation this option belongs to
         * @param name           the name of the option, as defined in the config
         * @param type           the {@link OptionType} of the option
         * @param convSection    the {@link ConfigurationSection} of the option
         * @throws QuestException if the configuration is invalid
         */
        @SuppressWarnings("PMD.CognitiveComplexity")
        protected ConversationOption(final ConversationID conversationID, final String name, final OptionType type, final ConfigurationSection convSection) throws QuestException {
            this.pack = conversationID.getPackage();
            this.conversationName = conversationID.getBaseID();
            this.optionName = name;
            this.type = type;
            final ConfigurationSection conv = convSection.getConfigurationSection(type.getIdentifier() + "." + name);

            if (conv == null) {
                pointers = new ArrayList<>();
                extendLinks = new ArrayList<>();
                return;
            }

            final String defaultLang = Config.getLanguage();

            parsePrefix(name, type, conv, defaultLang);
            parseText(name, type, conv, defaultLang);
            parseConditions(name, type, conv);
            parseEvents(name, type, conv);

            pointers = Arrays.stream(GlobalVariableResolver.resolve(pack, conv.getString("pointers", conv.getString("pointer", ""))).split(","))
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim).toList();

            extendLinks = Arrays.stream(GlobalVariableResolver.resolve(pack, conv.getString("extends", conv.getString("extend", ""))).split(","))
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim).toList();
        }

        private void parseEvents(final String name, final OptionType type, final ConfigurationSection conv) throws QuestException {
            try {
                for (final String rawEvent : GlobalVariableResolver.resolve(pack, conv.getString("events", conv.getString("event", ""))).split(",")) {
                    if (!Objects.equals(rawEvent, "")) {
                        events.add(new EventID(pack, rawEvent.trim()));
                    }
                }
            } catch (final ObjectNotFoundException e) {
                throw new QuestException("Error in '" + name + "' " + type.getReadable() + " option's events: "
                        + e.getMessage(), e);
            }
        }

        private void parseConditions(final String name, final OptionType type, final ConfigurationSection conv) throws QuestException {
            try {
                for (final String rawCondition : GlobalVariableResolver.resolve(pack, conv.getString("conditions", conv.getString("condition", ""))).split(",")) {
                    if (!rawCondition.isEmpty()) {
                        conditions.add(new ConditionID(pack, rawCondition.trim()));
                    }
                }
            } catch (final ObjectNotFoundException e) {
                throw new QuestException("Error in '" + name + "' " + type.getReadable() + " option's conditions: "
                        + e.getMessage(), e);
            }
        }

        //TODO: Consider removing this undocumented feature.
        @SuppressWarnings("PMD.CognitiveComplexity")
        private void parsePrefix(final String name, final OptionType type, final ConfigurationSection conv, final String defaultLang) throws QuestException {
            if (conv.contains("prefix")) {
                if (conv.isConfigurationSection("prefix")) {
                    for (final String lang : conv.getConfigurationSection("prefix").getKeys(false)) {
                        final String pref = GlobalVariableResolver.resolve(pack, conv.getConfigurationSection("prefix").getString(lang));
                        if (pref != null && !pref.isEmpty()) {
                            inlinePrefix.put(lang, pref);
                        }
                    }
                    if (!inlinePrefix.containsKey(defaultLang)) {
                        throw new QuestException("No default language for " + name + " " + type.getReadable()
                                + " prefix");
                    }
                } else {
                    final String pref = GlobalVariableResolver.resolve(pack, conv.getString("prefix"));
                    if (pref != null && !pref.isEmpty()) {
                        inlinePrefix.put(defaultLang, pref);
                    }
                }
            }
        }

        private void parseText(final String name, final OptionType type, final ConfigurationSection conv, final String defaultLang) throws QuestException {
            if (conv.contains("text")) {
                if (conv.isConfigurationSection("text")) {
                    for (final String lang : conv.getConfigurationSection("text").getKeys(false)) {
                        addConversationText(name, type, lang, "." + lang);
                    }
                    if (!text.containsKey(defaultLang)) {
                        throw new QuestException("No default language for " + name + " " + type.getReadable());
                    }
                } else {
                    addConversationText(name, type, defaultLang, "");
                }
            }
        }

        private void addConversationText(final String name, final OptionType type, final String lang, final String suffix) throws QuestException {
            final String convText = pack.getFormattedString("conversations." + conversationName + "." + type.getIdentifier() + "." + name + ".text" + suffix);
            if (convText == null) {
                throw new QuestException("No text for " + name + " " + type.getReadable());
            }
            text.put(lang, new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, convText));
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
         * Gets the inline prefix. Falls back to default language prefix.
         *
         * @param lang the language to get the prefix for
         * @return the prefix or null
         */
        @Nullable
        public String getInlinePrefix(final String lang) {
            final String langPrefix = inlinePrefix.get(lang);
            if (langPrefix != null) {
                return langPrefix;
            }
            return inlinePrefix.get(Config.getLanguage());
        }

        /**
         * Returns the text of this option in the given language.
         *
         * @param profile the profile of the player to get the text for
         * @param lang    the language to get the text in
         * @return the text of this option in the given language
         */
        public String getText(@Nullable final Profile profile, final String lang) {
            return getText(profile, lang, new ArrayList<>());
        }

        private String getText(@Nullable final Profile profile, final String lang, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return "";
            }
            optionPath.add(getName());

            final StringBuilder text = new StringBuilder(getText(lang, profile));

            if (profile != null) {
                for (final String extend : extendLinks) {
                    if (BetonQuest.conditions(profile, getOption(extend, type).getConditions())) {
                        text.append(getOption(extend, type).getText(profile, lang, optionPath));
                        break;
                    }
                }
            }

            return text.toString();
        }

        private @NotNull String getText(final String lang, @Nullable final Profile profile) {
            VariableString langText = this.text.get(lang);
            if (langText != null) {
                return langText.getString(profile);
            }
            langText = this.text.get(Config.getLanguage());
            if (langText != null) {
                return langText.getString(profile);
            }
            return "";
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
                if (BetonQuest.conditions(profile, getOption(extend, type).getConditions())) {
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
                        resolvedExtend = new ConversationOptionResolver(plugin, pack, convName, type, extend).resolve();
                    } catch (final QuestException | ObjectNotFoundException e) {
                        log.reportException(pack, e);
                        throw new IllegalStateException("Cannot ensure a valid conversation flow with unresolvable pointers.", e);
                    }

                    final ConversationData targetConvData = resolvedExtend.conversationData();
                    if (BetonQuest.conditions(profile, targetConvData.getOption(resolvedExtend.name(), type).getConditions())) {
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
    }
}
