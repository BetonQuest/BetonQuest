package org.betonquest.betonquest.conversation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.betonquest.betonquest.conversation.ConversationData.OptionType.NPC;
import static org.betonquest.betonquest.conversation.ConversationData.OptionType.PLAYER;

/**
 * Represents the data of the conversation.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class ConversationData {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(ConversationData.class);

    /**
     * All references made by this conversation's pointers to other conversations.
     */
    private final List<CrossConversationReference> externalPointers = new ArrayList<>();

    /**
     * The {@link BetonQuest} instance.
     */
    private final BetonQuest plugin;

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
    private EventID[] finalEvents;

    /**
     * The NPC options that the conversation can start from.
     */
    private List<String> startingOptions;

    /**
     * A map of all things the NPC can say during this conversation.
     * The key is the option name that can be pointed to.
     */
    private Map<String, Option> npcOptions;

    /**
     * A map of all things the player can say during this conversation.
     * The key is the option name that can be pointed to.
     */
    private Map<String, Option> playerOptions;

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
     * @param plugin      the plugin instance
     * @param pack        the package containing this conversation
     * @param name        the name of the conversation
     * @param convSection the configuration section of the conversation
     * @throws InstructionParseException when there is a syntax error in the defined conversation
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public ConversationData(final BetonQuest plugin, final QuestPackage pack, final String name, final ConfigurationSection convSection) throws InstructionParseException {
        this.plugin = plugin;
        this.pack = pack;
        final String pkg = pack.getQuestPath();
        log.debug(pack, String.format("Loading %s conversation from %s package", name, pkg));
        // package and name must be correct, it loads only existing
        // conversations
        convName = name;
        // get the main data
        if (convSection == null) {
            throw new InstructionParseException("The configuration is null!");
        }
        if (convSection.get("quester") == null) {
            throw new InstructionParseException("The 'quester' name is missing in the conversation file!");
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
                if (pref != null && !pref.equals("")) {
                    prefix.put(lang, pref);
                }
            }
        } else {
            final String pref = pack.getString("conversations." + convName + ".prefix");
            if (pref != null && !pref.equals("")) {
                prefix.put(Config.getLanguage(), pref);
            }
        }
        final String stop = pack.getString("conversations." + convName + ".stop");
        blockMovement = stop != null && stop.equalsIgnoreCase("true");
        final String rawConvIO = pack.getString("conversations." + convName + ".conversationIO", BetonQuest.getInstance().getPluginConfig().getString("default_conversation_IO", "menu,chest"));

        // check if all data is valid (or at least exist)
        for (final String s : rawConvIO.split(",")) {
            if (BetonQuest.getInstance().getConvIO(s.trim()) != null) {
                convIO = s.trim();
                break;
            }
        }
        if (convIO == null) {
            throw new InstructionParseException("No registered conversation IO found: " + rawConvIO);
        }

        final String rawInterceptor = pack.getString("conversations." + convName + ".interceptor", BetonQuest.getInstance().getPluginConfig().getString("default_interceptor", "simple"));
        for (final String s : rawInterceptor.split(",")) {
            if (BetonQuest.getInstance().getInterceptor(s.trim()) != null) {
                interceptor = s.trim();
                break;
            }
        }
        if (interceptor == null) {
            throw new InstructionParseException("No registered interceptor found: " + rawInterceptor);
        }

        if (quester.isEmpty()) {
            throw new InstructionParseException("Quester's name is not defined");
        }
        for (final String value : quester.values()) {
            if (value == null) {
                throw new InstructionParseException("Quester's name is not defined");
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
     * @throws InstructionParseException when a pointer to an external conversation could not be resolved
     */
    public void checkExternalPointers() throws InstructionParseException {
        for (final CrossConversationReference externalPointer : externalPointers) {

            final ConversationOptionResolverResult resolvedPointer = externalPointer.resolver().resolve();
            final QuestPackage targetPack = resolvedPointer.conversationData().pack;
            final String targetConvName = resolvedPointer.conversationData().convName;
            final String targetOptionName = resolvedPointer.optionName();

            final String sourceOption;
            if (externalPointer.sourceOption() == null) {
                sourceOption = "starting option";
            } else {
                sourceOption = "'" + externalPointer.sourceOption() + "' option";
            }

            final ConversationData conv;
            try {
                conv = BetonQuest.getInstance().getConversation(new ConversationID(targetPack, targetConvName));
            } catch (final ObjectNotFoundException e) {
                log.warn("Cross-conversation pointer in '" + externalPointer.sourcePack() + "' package, '" + externalPointer.sourceConv() + "' conversation, "
                        + sourceOption + " points to the '" + targetConvName
                        + "' conversation in the package '" + targetPack.getQuestPath() + "' but that conversation does not exist. Check your spelling!");
                continue;
            }

            // This is null if we refer to the starting options of a conversation
            if (targetOptionName == null) {
                continue;
            }

            if (conv.getText(Config.getLanguage(), targetOptionName, resolvedPointer.type()) == null) {
                log.warn(conv.pack, "External pointer in '" + externalPointer.sourcePack() + "' package, '" + externalPointer.sourceConv() + "' conversation, "
                        + sourceOption + " option points to '" + targetOptionName + "' NPC option in '" + targetConvName
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
     * @param optionType              the {@link org.betonquest.betonquest.conversation.ConversationData.OptionType} of the option
     * @return a {@link CrossConversationReference} pointing to the option
     * @throws InstructionParseException when the conversation could not be resolved
     */
    private CrossConversationReference resolvePointer(final QuestPackage pack, final String currentConversationName, final String currentOptionName, final OptionType optionType, final String option) throws InstructionParseException {
        final ConversationOptionResolver resolver = new ConversationOptionResolver(plugin, pack, currentConversationName, optionType, option);
        return new CrossConversationReference(pack, currentConversationName, currentOptionName, resolver);
    }

    private void parseOptions(final QuestPackage pack, final ConfigurationSection convSection) throws InstructionParseException {
        final String rawStartingOptions = pack.getString("conversations." + convName + ".first");
        if (rawStartingOptions == null || rawStartingOptions.isEmpty()) {
            throw new InstructionParseException("Starting options are not defined");
        }

        final String rawFinalEvents = pack.getString("conversations." + convName + ".final_events");
        if (rawFinalEvents == null || rawFinalEvents.isEmpty()) {
            finalEvents = new EventID[0];
        } else {
            final String[] array = rawFinalEvents.split(",");
            finalEvents = new EventID[array.length];
            for (int i = 0; i < array.length; i++) {
                try {
                    finalEvents[i] = new EventID(pack, array[i]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading final events: " + e.getMessage(), e);
                }
            }
        }

        loadNpcOptions(convSection);
        validateStartingOptions(pack, rawStartingOptions);

        loadPlayerOptions(convSection);
        validateNpcOptions();
        validatePlayerOptions(pack);
    }

    private void validatePlayerOptions(final QuestPackage pack) throws InstructionParseException {
        for (final Option option : playerOptions.values()) {
            for (final String pointer : option.getPointers()) {
                if (pointer.contains(".")) {
                    externalPointers.add(resolvePointer(pack, convName, option.getName(), OptionType.PLAYER, pointer));
                } else if (!npcOptions.containsKey(pointer)) {
                    throw new InstructionParseException(
                            String.format("Player option %s points to %s NPC option, but it does not exist",
                                    option.getName(), pointer));
                }
            }
            for (final String extend : option.getExtends()) {
                if (!playerOptions.containsKey(extend)) {
                    throw new InstructionParseException(
                            String.format("Player option %s extends %s, but it does not exist",
                                    option.getName(), extend));
                }
            }
        }
    }

    private void validateNpcOptions() throws InstructionParseException {
        for (final Option option : npcOptions.values()) {
            for (final String pointer : option.getPointers()) {
                if (pointer.contains(".")) {
                    externalPointers.add(resolvePointer(pack, convName, option.getName(), OptionType.NPC, pointer));
                } else if (!playerOptions.containsKey(pointer)) {
                    throw new InstructionParseException(
                            String.format("NPC option %s points to %s player option, but it does not exist",
                                    option.getName(), pointer));
                }
            }
            for (final String extend : option.getExtends()) {
                if (!npcOptions.containsKey(extend)) {
                    throw new InstructionParseException(
                            String.format("NPC option %s extends %s, but it does not exist",
                                    option.getName(), extend));
                }
            }
        }
    }

    /**
     * Checks if all starting options point to existing NPC options.
     *
     * @param pack               the package containing this conversation
     * @param rawStartingOptions the raw starting options
     * @throws InstructionParseException when the conversation could not be resolved
     */
    private void validateStartingOptions(final QuestPackage pack, final String rawStartingOptions) throws InstructionParseException {
        startingOptions = Arrays.stream(rawStartingOptions.split(",")).map(String::trim).toList();

        for (final String startingOption : startingOptions) {
            if (startingOption.contains(".")) {
                externalPointers.add(resolvePointer(pack, convName, null, NPC, startingOption));
            } else if (!npcOptions.containsKey(startingOption)) {
                throw new InstructionParseException("Starting option " + startingOption + " does not exist");
            }
        }
    }

    private void loadPlayerOptions(final ConfigurationSection conv) throws InstructionParseException {
        final ConfigurationSection playerSection = conv.getConfigurationSection("player_options");
        playerOptions = new HashMap<>();
        if (playerSection != null) {
            for (final String key : playerSection.getKeys(false)) {
                playerOptions.put(key, new Option(key, PLAYER, conv));
            }
        }
    }

    private void loadNpcOptions(final ConfigurationSection convSection) throws InstructionParseException {
        final ConfigurationSection npcSection = convSection.getConfigurationSection("NPC_options");
        if (npcSection == null) {
            throw new InstructionParseException("NPC_options section not defined");
        }
        npcOptions = new HashMap<>();
        for (final String key : npcSection.getKeys(false)) {
            npcOptions.put(key, new Option(key, NPC, convSection));
        }
    }

    /**
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
    public String getPrefix(final String lang, final String option) {
        // get prefix from an option
        if (option != null) {
            String pref = npcOptions.get(option).getInlinePrefix(lang);
            if (pref == null) {
                pref = npcOptions.get(option).getInlinePrefix(Config.getLanguage());
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
     * @return the final events
     */
    public EventID[] getFinalEvents() {
        return Arrays.copyOf(finalEvents, finalEvents.length);
    }

    /**
     * @return the starting options
     */
    public String[] getStartingOptions() {
        return Arrays.copyOf(startingOptions.toArray(new String[0]), startingOptions.size());
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

    public String getText(final String lang, final String option, final OptionType type) {
        return getText(null, lang, option, type);
    }

    public String getText(final Profile profile, final String lang, final String option, final OptionType type) {
        final Option opt;
        if (type == NPC) {
            opt = npcOptions.get(option);
        } else {
            opt = playerOptions.get(option);
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
        final Map<String, Option> options = type == NPC ? npcOptions : playerOptions;
        return options.get(option).getConditions();
    }

    public EventID[] getEventIDs(final Profile profile, final String option, final OptionType type) {
        final Map<String, Option> options;
        if (type == NPC) {
            options = npcOptions;
        } else {
            options = playerOptions;
        }
        if (options.containsKey(option)) {
            return options.get(option).getEvents(profile);
        } else {
            return new EventID[0];
        }
    }

    public String[] getPointers(final Profile profile, final String option, final OptionType type) {
        final Map<String, Option> options;
        if (type == NPC) {
            options = npcOptions;
        } else {
            options = playerOptions;
        }
        return options.get(option).getPointers(profile);
    }

    public Option getOption(final String option, final OptionType type) {
        return type == NPC ? npcOptions.get(option) : playerOptions.get(option);
    }

    /**
     * Check if conversation has at least one valid option for player
     *
     * @param profile the {@link Profile} of the player
     * @return True, if the player can star the conversation.
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public boolean isReady(final Profile profile) throws ObjectNotFoundException {
        for (final String option : getStartingOptions()) {
            final String convName;
            final String optionName;
            if (option.contains(".")) {
                final String[] parts = option.split("\\.");
                convName = parts[0];
                optionName = parts[1];
            } else {
                convName = getName();
                optionName = option;
            }
            final QuestPackage pack = Config.getPackages().get(getPack());
            final ConversationData currentData = BetonQuest.getInstance().getConversation(new ConversationID(pack, convName));
            if (BetonQuest.conditions(profile, currentData.getConditionIDs(optionName, NPC))) {
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

        private final String identifier;

        private final String readable;

        OptionType(final String identifier, final String readable) {
            this.identifier = identifier;
            this.readable = readable;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getReadable() {
            return readable;
        }
    }

    /**
     * Represents a conversation option.
     */
    private class Option {
        private final String name;

        private final OptionType type;

        private final Map<String, String> inlinePrefix = new HashMap<>();

        private final Map<String, String> text = new HashMap<>();

        private final List<ConditionID> conditions = new ArrayList<>();

        private final List<EventID> events = new ArrayList<>();

        private final List<String> pointers;

        private final List<String> extendLinks;

        @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
        protected Option(final String name, final OptionType type, final ConfigurationSection convSection) throws InstructionParseException {
            this.name = name;
            this.type = type;
            final ConfigurationSection conv = convSection.getConfigurationSection(type.getIdentifier() + "." + name);

            if (conv == null) {
                pointers = new ArrayList<>();
                extendLinks = new ArrayList<>();
                return;
            }

            final String defaultLang = Config.getLanguage();
            // Prefix
            if (conv.contains("prefix")) {
                if (conv.isConfigurationSection("prefix")) {
                    for (final String lang : conv.getConfigurationSection("prefix").getKeys(false)) {
                        final String pref = GlobalVariableResolver.resolve(pack, conv.getConfigurationSection("prefix").getString(lang));
                        if (pref != null && !pref.isEmpty()) {
                            inlinePrefix.put(lang, pref);
                        }
                    }
                    if (!inlinePrefix.containsKey(defaultLang)) {
                        throw new InstructionParseException("No default language for " + name + " " + type.getReadable()
                                + " prefix");
                    }
                } else {
                    final String pref = GlobalVariableResolver.resolve(pack, conv.getString("prefix"));
                    if (pref != null && !pref.isEmpty()) {
                        inlinePrefix.put(defaultLang, pref);
                    }
                }
            }

            // Text
            if (conv.contains("text")) {
                if (conv.isConfigurationSection("text")) {
                    for (final String lang : conv.getConfigurationSection("text").getKeys(false)) {
                        text.put(lang, pack.getFormattedString("conversations." + convName + "." + type.getIdentifier() + "." + name + ".text."
                                + lang));
                    }
                    if (!text.containsKey(defaultLang)) {
                        throw new InstructionParseException("No default language for " + name + " " + type.getReadable());
                    }
                } else {
                    text.put(defaultLang, pack.getFormattedString("conversations." + convName + "." + type.getIdentifier() + "." + name + ".text"));
                }

                final List<String> variables = new ArrayList<>();
                for (final String theText : text.values()) {
                    if (theText == null || theText.isEmpty()) {
                        throw new InstructionParseException("Text not defined in " + type.getReadable() + " " + name);
                    }
                    // variables are possibly duplicated because there probably is
                    // the same variable in every language
                    final List<String> possiblyDuplicatedVariables = BetonQuest.resolveVariables(theText);
                    for (final String possiblyDuplicatedVariable : possiblyDuplicatedVariables) {
                        if (variables.contains(possiblyDuplicatedVariable)) {
                            continue;
                        }
                        variables.add(possiblyDuplicatedVariable);
                    }
                }
                for (final String variable : variables) {
                    try {
                        BetonQuest.createVariable(pack, variable);
                    } catch (final InstructionParseException e) {
                        throw new InstructionParseException("Error while creating '" + variable + "' variable: "
                                + e.getMessage(), e);
                    }
                }
            }

            // Conditions
            try {
                for (final String rawCondition : GlobalVariableResolver.resolve(pack, conv.getString("conditions", conv.getString("condition", ""))).split(",")) {
                    if (!rawCondition.isEmpty()) {
                        conditions.add(new ConditionID(pack, rawCondition.trim()));
                    }
                }
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error in '" + name + "' " + type.getReadable() + " option's conditions: "
                        + e.getMessage(), e);
            }

            // Events
            try {
                for (final String rawEvent : GlobalVariableResolver.resolve(pack, conv.getString("events", conv.getString("event", ""))).split(",")) {
                    if (!Objects.equals(rawEvent, "")) {
                        events.add(new EventID(pack, rawEvent.trim()));
                    }
                }
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error in '" + name + "' " + type.getReadable() + " option's events: "
                        + e.getMessage(), e);
            }

            // Pointers
            pointers = Arrays.stream(GlobalVariableResolver.resolve(pack, conv.getString("pointers", conv.getString("pointer", ""))).split(","))
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim).toList();

            extendLinks = Arrays.stream(GlobalVariableResolver.resolve(pack, conv.getString("extends", conv.getString("extend", ""))).split(","))
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim).toList();
        }

        public String getName() {
            return name;
        }

        public String getInlinePrefix(final String lang) {
            String thePrefix = inlinePrefix.get(lang);
            if (thePrefix == null) {
                thePrefix = inlinePrefix.get(Config.getLanguage());
            }
            return thePrefix;
        }

        public String getText(final Profile profile, final String lang) {
            return getText(profile, lang, new ArrayList<>());
        }

        public String getText(final Profile profile, final String lang, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return "";
            }
            optionPath.add(getName());

            final StringBuilder ret = new StringBuilder(text.getOrDefault(lang, text.getOrDefault(Config.getLanguage(), "")));

            if (profile != null) {
                for (final String extend : extendLinks) {
                    if (BetonQuest.conditions(profile, getOption(extend, type).getConditions())) {
                        ret.append(getOption(extend, type).getText(profile, lang, optionPath));
                        break;
                    }
                }
            }

            return ret.toString();
        }

        public List<ConditionID> getConditions() {
            return new ArrayList<>(conditions);
        }

        public EventID[] getEvents(final Profile profile) {
            return getEvents(profile, new ArrayList<>());
        }

        public EventID[] getEvents(final Profile profile, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return new EventID[0];
            }
            optionPath.add(getName());

            final List<EventID> ret = new ArrayList<>(events);

            for (final String extend : extendLinks) {
                if (BetonQuest.conditions(profile, getOption(extend, type).getConditions())) {
                    ret.addAll(Arrays.asList(getOption(extend, type).getEvents(profile, optionPath)));
                    break;
                }
            }

            return ret.toArray(new EventID[0]);
        }

        public String[] getPointers() {
            return getPointers(null);
        }

        public String[] getPointers(final Profile profile) {
            return getPointers(profile, new ArrayList<>());
        }

        public String[] getPointers(final Profile profile, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return new String[0];
            }
            optionPath.add(getName());

            final List<String> ret = new ArrayList<>(pointers);

            if (profile != null) {
                for (final String extend : extendLinks) {
                    if (BetonQuest.conditions(profile, getOption(extend, type).getConditions())) {
                        ret.addAll(Arrays.asList(getOption(extend, type).getPointers(profile, optionPath)));
                        break;
                    }
                }
            }

            return ret.toArray(new String[0]);
        }

        public String[] getExtends() {
            return extendLinks.toArray(new String[0]);
        }
    }
}
