/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.conversation;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Represents the data of the conversation.
 *
 * @author Jakub Sapalski
 */
public class ConversationData {

    private static ArrayList<String> externalPointers = new ArrayList<>();

    private ConfigPackage pack;
    private String convName;

    private HashMap<String, String> quester = new HashMap<>(); // maps for multiple languages
    private HashMap<String, String> prefix = new HashMap<>(); // global conversation prefix
    private EventID[] finalEvents;
    private String[] startingOptions;
    private boolean blockMovement;
    private String convIO;
    private String interceptor;

    private HashMap<String, Option> npcOptions;
    private HashMap<String, Option> playerOptions;

    /**
     * Loads conversation from package.
     *
     * @param pack the package containing this conversation
     * @param name the name of the conversation
     * @throws InstructionParseException when there is a syntax error in the defined conversation
     */
    public ConversationData(final ConfigPackage pack, final String name) throws InstructionParseException {
        this.pack = pack;
        final String pkg = pack.getName();
        LogUtils.getLogger().log(Level.FINE, String.format("Loading %s conversation from %s package", name, pkg));
        // package and name must be correct, it loads only existing
        // conversations
        convName = name;
        // get the main data
        final FileConfiguration conv = pack.getConversation(name).getConfig();
        if (conv.isConfigurationSection("quester")) {
            //noinspection ConstantConditions
            for (final String lang : conv.getConfigurationSection("quester").getKeys(false)) {
                quester.put(lang, pack.getString("conversations." + name + ".quester." + lang));
            }
        } else {
            quester.put(Config.getLanguage(), pack.getString("conversations." + name + ".quester"));
        }
        if (conv.isConfigurationSection("prefix")) {
            //noinspection ConstantConditions
            for (final String lang : conv.getConfigurationSection("prefix").getKeys(false)) {
                final String pref = pack.getString("conversations." + name + ".prefix." + lang);
                if (pref != null && !pref.equals("")) {
                    prefix.put(lang, pref);
                }
            }
        } else {
            final String pref = pack.getString("conversations." + name + ".prefix");
            if (pref != null && !pref.equals("")) {
                prefix.put(Config.getLanguage(), pref);
            }
        }
        final String stop = pack.getString("conversations." + name + ".stop");
        blockMovement = stop != null && stop.equalsIgnoreCase("true");
        final String rawConvIO = pack.getString("conversations." + name + ".conversationIO", BetonQuest.getInstance().getConfig().getString("default_conversation_IO", "menu,chest"));

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

        final String rawInterceptor = pack.getString("conversations." + name + ".interceptor", BetonQuest.getInstance().getConfig().getString("default_interceptor", "simple"));
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
        final String rawStartingOptions = pack.getString("conversations." + name + ".first");
        if (rawStartingOptions == null || rawStartingOptions.equals("")) {
            throw new InstructionParseException("Starting options are not defined");
        }
        final String rawFinalEvents = pack.getString("conversations." + name + ".final_events");
        if (rawFinalEvents == null || rawFinalEvents.equals("")) {
            finalEvents = new EventID[0];
        } else {
            final String[] array = rawFinalEvents.split(",");
            finalEvents = new EventID[array.length];
            for (int i = 0; i < array.length; i++) {
                try {
                    finalEvents[i] = new EventID(pack, array[i]);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading final events: " + e.getMessage(), e);
                }
            }
        }
        // load all NPC options
        final ConfigurationSection npcSection = pack.getConversation(name).getConfig().getConfigurationSection("NPC_options");
        if (npcSection == null) {
            throw new InstructionParseException("NPC_options section not defined");
        }
        npcOptions = new HashMap<>();
        for (final String key : npcSection.getKeys(false)) {
            npcOptions.put(key, new NPCOption(key));
        }
        // check if all starting options point to existing NPC options
        startingOptions = rawStartingOptions.split(",");
        // remove spaces between the options
        for (int i = 0; i < startingOptions.length; i++) {
            startingOptions[i] = startingOptions[i].trim();
        }
        for (final String startingOption : startingOptions) {
            if (startingOption.contains(".")) {
                final String entirePointer = pack.getName() + "." + convName + ".<starting_option>."
                        + startingOption;
                externalPointers.add(entirePointer);
            } else if (!npcOptions.containsKey(startingOption)) {
                throw new InstructionParseException("Starting option " + startingOption + " does not exist");
            }
        }
        // load all Player options
        final ConfigurationSection playerSection = pack.getConversation(name).getConfig()
                .getConfigurationSection("player_options");
        playerOptions = new HashMap<>();
        if (playerSection != null) {
            for (final String key : playerSection.getKeys(false)) {
                playerOptions.put(key, new PlayerOption(key));
            }
        }

        // check if every pointer points to existing option.
        for (final Option option : npcOptions.values()) {
            for (final String pointer : option.getPointers()) {
                if (!playerOptions.containsKey(pointer)) {
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
        for (final Option option : playerOptions.values()) {
            for (final String pointer : option.getPointers()) {
                if (pointer.contains(".")) {
                    final String entirePointer = pack.getName() + "." + convName + "." + option.getName() + "." + pointer;
                    externalPointers.add(entirePointer);
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

        // done, everything will work
        LogUtils.getLogger().log(Level.FINE, String.format("Conversation loaded: %d NPC options and %d player options", npcOptions.size(),
                playerOptions.size()));
    }

    /**
     * Checks if external pointers point to valid options. It cannot be checked
     * when constructing ConversationData objects because conversations that are
     * being pointed to may not yet exist.
     * <p>
     * This method should be called when all conversations are loaded. It will
     * not throw any exceptions, just display errors in the console.
     */
    public static void postEnableCheck() {
        for (final String externalPointer : externalPointers) {
            final String[] parts = externalPointer.split("\\.");
            final String packName = parts[0];
            final String sourceConv = parts[1];
            final String sourceOption = parts[2];
            final String targetConv = parts[3];
            final String targetOption = parts[4];
            final ConversationData conv = BetonQuest.getInstance().getConversation(packName + "." + targetConv);
            if (conv == null) {
                LogUtils.getLogger().log(Level.WARNING, "External pointer in '" + packName + "' package, '" + sourceConv + "' conversation, "
                        + (sourceOption.equals("<starting_option>") ? "starting option"
                        : "'" + sourceOption + "' player option")
                        + " points to '" + targetConv
                        + "' conversation, but it does not even exist. Check your spelling!");
                continue;
            }
            if (conv.getText(Config.getLanguage(), targetOption, OptionType.NPC) == null) {
                LogUtils.getLogger().log(Level.WARNING, "External pointer in '" + packName + "' package, '" + sourceConv + "' conversation, "
                        + (sourceOption.equals("<starting_option>") ? "starting option"
                        : "'" + sourceOption + "' player option")
                        + " points to '" + targetOption + "' NPC option in '" + targetConv
                        + "' conversation, but it does not exist.");
            }
        }
        externalPointers.clear();
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
     * @param lang language of quester's name
     * @return the quester's name
     */
    public String getQuester(final String lang) {
        String text = quester.get(lang);
        if (text == null) {
            text = quester.get(Config.getLanguage());
        }
        return text;
    }

    /**
     * @return the final events
     */
    public EventID[] getFinalEvents() {
        return finalEvents;
    }

    /**
     * @return the starting options
     */
    public String[] getStartingOptions() {
        return startingOptions;
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

    public String getText(final String playerID, final String lang, final String option, final OptionType type) {
        final Option opt;
        if (type == OptionType.NPC) {
            opt = npcOptions.get(option);
        } else {
            opt = playerOptions.get(option);
        }
        if (opt == null) {
            return null;
        }
        return opt.getText(playerID, lang);
    }

    /**
     * @return the name of the package
     */
    public String getPackName() {
        return pack.getName();
    }

    public ConditionID[] getConditionIDs(final String option, final OptionType type) {
        final HashMap<String, Option> options;
        if (type == OptionType.NPC) {
            options = npcOptions;
        } else {
            options = playerOptions;
        }
        return options.get(option).getConditions();
    }

    public EventID[] getEventIDs(final String playerID, final String option, final OptionType type) {
        final HashMap<String, Option> options;
        if (type == OptionType.NPC) {
            options = npcOptions;
        } else {
            options = playerOptions;
        }
        if (options.containsKey(option)) {
            return options.get(option).getEvents(playerID);
        } else {
            return new EventID[0];
        }
    }

    public String[] getPointers(final String playerID, final String option, final OptionType type) {
        final HashMap<String, Option> options;
        if (type == OptionType.NPC) {
            options = npcOptions;
        } else {
            options = playerOptions;
        }
        return options.get(option).getPointers(playerID);
    }

    public Option getOption(final String option, final OptionType type) {
        return type == OptionType.NPC ? npcOptions.get(option) : playerOptions.get(option);
    }

    /**
     * Check if conversation has at least one valid option for player
     */
    public boolean isReady(final String playerID) {
        options:
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
            final ConfigPackage pack = Config.getPackages().get(getPackName());
            final ConversationData currentData = BetonQuest.getInstance().getConversation(pack.getName() + "." + convName);
            for (final ConditionID condition : currentData.getConditionIDs(optionName, ConversationData.OptionType.NPC)) {
                if (!BetonQuest.condition(playerID, condition)) {
                    continue options;
                }
            }
            return true;
        }
        return false;
    }

    public enum OptionType {
        NPC, PLAYER
    }

    /**
     * Represents an option
     */
    private abstract class Option {

        private String name;
        private OptionType type;
        private HashMap<String, String> inlinePrefix = new HashMap<>();

        private HashMap<String, String> text = new HashMap<>();
        private List<ConditionID> conditions = new ArrayList<>();
        private List<EventID> events = new ArrayList<>();
        private List<String> pointers;
        private List<String> extendLinks;

        public Option(final String name, final String type, final String visibleType) throws InstructionParseException {
            this.name = name;
            this.type = type.equals("player_options") ? OptionType.PLAYER : OptionType.NPC;
            final ConfigurationSection conv = pack.getConversation(convName).getConfig().getConfigurationSection(type + "." + name);

            if (conv == null) {
                return;
            }

            final String defaultLang = Config.getLanguage();
            // Prefix
            if (conv.contains("prefix")) {
                if (conv.isConfigurationSection("prefix")) {
                    //noinspection ConstantConditions
                    for (final String lang : conv.getConfigurationSection("prefix").getKeys(false)) {
                        final String pref = pack.subst(conv.getConfigurationSection("prefix").getString(lang));
                        if (pref != null && !pref.equals("")) {
                            inlinePrefix.put(lang, pref);
                        }
                    }
                    if (!inlinePrefix.containsKey(defaultLang)) {
                        throw new InstructionParseException("No default language for " + name + " " + visibleType
                                + " prefix");
                    }
                } else {
                    final String pref = pack.subst(conv.getString("prefix"));
                    if (pref != null && !pref.equals("")) {
                        inlinePrefix.put(defaultLang, pref);
                    }
                }
            }

            // Text
            if (conv.contains("text")) {
                if (conv.isConfigurationSection("text")) {
                    //noinspection ConstantConditions
                    for (final String lang : conv.getConfigurationSection("text").getKeys(false)) {
                        text.put(lang, pack.getFormattedString("conversations." + convName + "." + type + "." + name + ".text."
                                + lang));
                    }
                    if (!text.containsKey(defaultLang)) {
                        throw new InstructionParseException("No default language for " + name + " " + visibleType);
                    }
                } else {
                    text.put(defaultLang, pack.getFormattedString("conversations." + convName + "." + type + "." + name + ".text"));
                }

                final ArrayList<String> variables = new ArrayList<>();
                for (final String theText : text.values()) {
                    if (theText == null || theText.equals("")) {
                        throw new InstructionParseException("Text not defined in " + visibleType + " " + name);
                    }
                    // variables are possibly duplicated because there probably is
                    // the same variable in every language
                    final ArrayList<String> possiblyDuplicatedVariables = BetonQuest.resolveVariables(theText);
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
                    } catch (InstructionParseException e) {
                        throw new InstructionParseException("Error while creating '" + variable + "' variable: "
                                + e.getMessage(), e);
                    }
                }
            }

            // Conditions
            try {
                for (final String rawCondition : pack.subst(conv.getString("conditions", conv.getString("condition", ""))).split(",")) {
                    if (!Objects.equals(rawCondition, "")) {
                        conditions.add(new ConditionID(pack, rawCondition.trim()));
                    }
                }
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Error in '" + name + "' " + visibleType + " option's conditions: "
                        + e.getMessage(), e);
            }

            // Events
            try {
                for (final String rawEvent : pack.subst(conv.getString("events", conv.getString("event", ""))).split(",")) {
                    if (!Objects.equals(rawEvent, "")) {
                        events.add(new EventID(pack, rawEvent.trim()));
                    }
                }
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Error in '" + name + "' " + visibleType + " option's events: "
                        + e.getMessage(), e);
            }

            // Pointers
            pointers = Arrays.stream(pack.subst(conv.getString("pointers", conv.getString("pointer", ""))).split(","))
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim)
                    .collect(Collectors.toList());


            extendLinks = Arrays.stream(pack.subst(conv.getString("extends", conv.getString("extend", ""))).split(","))
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim)
                    .collect(Collectors.toList());
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

        public String getText(final String playerID, final String lang) {
            return getText(playerID, lang, new ArrayList<>());
        }

        public String getText(final String playerID, final String lang, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return "";
            }
            optionPath.add(getName());

            final StringBuilder ret = new StringBuilder(text.getOrDefault(lang, text.getOrDefault(Config.getLanguage(), "")));

            if (playerID != null) {
                extend:
                for (final String extend : extendLinks) {
                    for (final ConditionID condition : getOption(extend, type).getConditions()) {
                        if (!BetonQuest.condition(playerID, condition)) {
                            continue extend;
                        }
                    }
                    ret.append(getOption(extend, type).getText(playerID, lang, optionPath));
                    break;
                }
            }

            return ret.toString();
        }

        public ConditionID[] getConditions() {
            return getConditions(new ArrayList<>());
        }

        public ConditionID[] getConditions(final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return new ConditionID[0];
            }
            optionPath.add(getName());

            final List<ConditionID> ret = new ArrayList<>(conditions);

            return ret.toArray(new ConditionID[0]);
        }

        public EventID[] getEvents(final String playerID) {
            return getEvents(playerID, new ArrayList<>());
        }

        public EventID[] getEvents(final String playerID, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return new EventID[0];
            }
            optionPath.add(getName());

            final List<EventID> ret = new ArrayList<>(events);

            extend:
            for (final String extend : extendLinks) {
                for (final ConditionID condition : getOption(extend, type).getConditions()) {
                    if (!BetonQuest.condition(playerID, condition)) {
                        continue extend;
                    }
                }
                ret.addAll(Arrays.asList(getOption(extend, type).getEvents(playerID, optionPath)));
                break;
            }

            return ret.toArray(new EventID[0]);
        }

        public String[] getPointers() {
            return getPointers(null);
        }

        public String[] getPointers(final String playerID) {
            return getPointers(playerID, new ArrayList<>());
        }

        public String[] getPointers(final String playerID, final List<String> optionPath) {
            // Prevent infinite loops
            if (optionPath.contains(getName())) {
                return new String[0];
            }
            optionPath.add(getName());

            final List<String> ret = new ArrayList<>(pointers);

            if (playerID != null) {
                extend:
                for (final String extend : extendLinks) {
                    for (final ConditionID condition : getOption(extend, type).getConditions()) {
                        if (!BetonQuest.condition(playerID, condition)) {
                            continue extend;
                        }
                    }
                    ret.addAll(Arrays.asList(getOption(extend, type).getPointers(playerID, optionPath)));
                    break;
                }
            }

            return ret.toArray(new String[0]);
        }

        public String[] getExtends() {
            return extendLinks.toArray(new String[0]);
        }
    }

    /**
     * Represents an option which can be choosen by the Player
     */
    private class PlayerOption extends Option {
        public PlayerOption(final String name) throws InstructionParseException {
            super(name, "player_options", "player option");
        }
    }

    /**
     * Represents an option which can be choosen by the NPC
     */
    private class NPCOption extends Option {
        public NPCOption(final String name) throws InstructionParseException {
            super(name, "NPC_options", "NPC option");
        }
    }
}
