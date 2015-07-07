/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.core;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;


/**
 * Represents the data of the conversation.
 * 
 * @author Jakub Sapalski
 */
public class ConversationData {
    
    private ConfigPackage pack;
    private String convName;
    
    private HashMap<String, String> quester = new HashMap<>(); // maps for multiple languages
    private HashMap<String, String> unknown = new HashMap<>();
    private String[] finalEvents;
    private String[] startingOptions;
    private boolean blockMovement;
    
    private HashMap<String, Option> NPCOptions;
    private HashMap<String, Option> playerOptions;

    /**
     * Loads conversation from package.
     * 
     * @param pkg
     *          Name of the package
     * @param
     *          Name of the conversation
     * @throws InstructionParseException 
     */
    public ConversationData(String pkg, String name) throws InstructionParseException {
        Debug.info(String.format("Loading %s conversation from %s package", name, pkg));
        // package and name must be correct, it loads only existing conversations
        convName = name;
        pack = Config.getPackage(pkg);
        // get the main data
        FileConfiguration conv = pack.getConversation(name).getConfig();
        if (conv.isConfigurationSection("quester")) {
            for (String lang : conv.getConfigurationSection("quester").getKeys(false)) {
                quester.put(lang, pack.getString("conversations." + name + ".quester." + lang));
            }
        } else {
            quester.put(Config.getLanguage(), pack.getString("conversations." + name + ".quester"));
        }
        if (conv.isConfigurationSection("unknown")) {
            for (String lang : conv.getConfigurationSection("unknown").getKeys(false)) {
                unknown.put(lang, pack.getString("conversations." + name + ".unknown." + lang));
            }
        } else {
            unknown.put(Config.getLanguage(), pack.getString("conversations." + name + ".unknown"));
        }
        String rawFinalEvents = pack.getString("conversations." + name + ".final_events");
        String rawStartingOptions = pack.getString("conversations." + name + ".first");
        String stop = pack.getString("conversations." + name + ".stop");
        blockMovement = stop != null && stop.equalsIgnoreCase("true");
        // check if all data is valid (or at least exist)
        if (quester == null || quester.equals("")) {
            throw new InstructionParseException("Quester's name is not defined");
        }
        if (unknown == null || unknown.equals("")) {
            throw new InstructionParseException("\"Unknown\" text is not defined");
        }
        if (rawStartingOptions == null || rawStartingOptions.equals("")) {
            throw new InstructionParseException("Starting options are not defined");
        }
        startingOptions = rawStartingOptions.split(",");
        if (rawFinalEvents != null && !rawFinalEvents.equals("")) {
            finalEvents = rawFinalEvents.split(",");
        } else {
            finalEvents = new String[]{};
        }
        // attach package names
        for (int i = 0; i < finalEvents.length; i++) {
            if (!finalEvents[i].contains(".")) {
                finalEvents[i] = pack.getName() + "." + finalEvents[i];
            }
        }
        // load all NPC options
        ConfigurationSection NPCSection = pack.getConversation(name)
                .getConfig().getConfigurationSection("NPC_options");
        if (NPCSection == null) {
            throw new InstructionParseException("NPC_options section not defined");
        }
        NPCOptions = new HashMap<>();
        for (String key : NPCSection.getKeys(false)) {
            NPCOptions.put(key, new NPCOption(key));
        }
        // load all Player options
        ConfigurationSection playerSection = pack.getConversation(name)
                .getConfig().getConfigurationSection("player_options");
        playerOptions = new HashMap<>();
        if (playerSection != null) {
            for (String key : playerSection.getKeys(false)) {
                playerOptions.put(key, new PlayerOption(key));
            }
        }
        // check if every pointer points to existing option
        for (Option option : NPCOptions.values()) {
            for (String pointer : option.getPointers()) {
                if (!playerOptions.containsKey(pointer)) {
                    throw new InstructionParseException(String.format(
                            "NPC option %s points to %s player option, but it does not exist",
                            option.getName(), pointer));
                }
            }
        }
        for (Option option : playerOptions.values()) {
            for (String pointer : option.getPointers()) {
                if (!NPCOptions.containsKey(pointer)) {
                    throw new InstructionParseException(String.format(
                            "Player option %s points to %s NPC option, but it does not exist",
                            option.getName(), pointer));
                }
            }
        }
        // done, everything will work
        Debug.info(String.format(
                "Conversation loaded: %d NPC options and %d player options",
                NPCOptions.size(), playerOptions.size())
        );
    }
    
    /**
     * @param lang
     *          language of quester's name
     * @return the quester's name
     */
    public String getQuester(String lang) {
        String text = quester.get(lang);
        if (text == null) {
            text = quester.get(Config.getLanguage());
        }
        return text;
    }
    
    /**
     * @param lang
     *          language of quester's response
     * @return the unknown message
     */
    public String getUnknown(String lang) {
        String text = unknown.get(lang);
        if (text == null) {
            text = unknown.get(Config.getLanguage());
        }
        return text;
    }
    
    /**
     * @return the final events
     */
    public String[] getFinalEvents() {
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
    
    public String getText(String lang, String option, OptionType type) {
        if (type == OptionType.NPC) {
            return NPCOptions.get(option).getText(lang);
        } else {
            return playerOptions.get(option).getText(lang);
        }
    }
    
    public String[] getData(String option, OptionType type, RequestType req) {
        HashMap<String, Option> options;
        if (type == OptionType.NPC) {
            options = NPCOptions;
        } else {
            options = playerOptions;
        }
        switch (req) {
            case CONDITION:
                return options.get(option).getConditions();
            case EVENT:
                return options.get(option).getEvents();
            case POINTER:
                return options.get(option).getPointers();
        }
        return null;
    }
    
    private interface Option {
        public String getName();
        public String getText(String lang);
        public String[] getConditions();
        public String[] getEvents();
        public String[] getPointers();
    }

    /**
     * Represents an option which can be said by the NPC
     */
    private class NPCOption implements Option {
        
        private String name;
        
        private HashMap<String, String> text = new HashMap<>();
        private String[] conditions;
        private String[] events;
        private String[] pointers;
        
        public NPCOption(String name) throws InstructionParseException {
            this.name = name;
            String defaultLang = Config.getLanguage();
            if (pack.getConversation(convName).getConfig()
                    .isConfigurationSection("NPC_options." + name + ".text")) {
                for (String lang : pack.getConversation(convName).getConfig()
                        .getConfigurationSection("NPC_options." + name + ".text")
                        .getKeys(false)) {
                    text.put(lang, pack.getString("conversations." + convName
                            + ".NPC_options." + name + ".text." + lang));
                }
                if (!text.containsKey(defaultLang)) {
                    throw new InstructionParseException(
                            "No default language for " + name + " NPC option");
                }
            } else {
                text.put(defaultLang, pack.getString("conversations." + convName
                        + ".NPC_options." + name + ".text"));
            };
            for (String theText : text.values()) {
                if (theText == null || theText.equals("")) throw new InstructionParseException(String.format(
                        "Text not defined in NPC option %s", name));
            }
            String rawConditions = pack.getString("conversations." + convName + ".NPC_options." + name + ".conditions");
            if (rawConditions != null && !rawConditions.equals("")) {
                conditions = rawConditions.split(",");
            } else {
                conditions = new String[]{};
            }
            for (int i = 0; i < conditions.length; i++) {
                if (!conditions[i].contains(".")) {
                    conditions[i] = pack.getName() + "." + conditions[i];
                }
            }
            String rawEvents = pack.getString("conversations." + convName + ".NPC_options." + name + ".events");
            if (rawEvents != null && !rawEvents.equals("")) {
                events = rawEvents.split(",");
            } else {
                events = new String[]{};
            }
            for (int i = 0; i < events.length; i++) {
                if (!events[i].contains(".")) {
                    events[i] = pack.getName() + "." + events[i];
                }
            }
            String rawPointers = pack.getString("conversations." + convName + ".NPC_options." + name + ".pointer");
            if (rawPointers != null && !rawPointers.equals("")) {
                pointers = rawPointers.split(",");
            } else {
                pointers = new String[]{};
            }
        }
        
        public String getName() {
            return name;
        }
        
        public String getText(String lang) {
            String theText = text.get(lang);
            if (theText == null) {
                theText = text.get(Config.getLanguage());
            }
            return theText;
        }
        
        public String[] getConditions() {
            return conditions;
        }
        
        public String[] getEvents() {
            return events;
        }
        
        public String[] getPointers() {
            return pointers;
        }
    }
    
    /**
     * Represents an option which can be choosen by the Player
     */
    private class PlayerOption implements Option {
        
        private String name;
        
        private HashMap<String, String> text = new HashMap<>();
        private String[] conditions;
        private String[] events;
        private String[] pointers;
        
        public PlayerOption(String name) throws InstructionParseException {
            this.name = name;
            String defaultLang = Config.getLanguage();
            if (pack.getConversation(convName).getConfig()
                    .isConfigurationSection("player_options." + name + ".text")) {
                for (String lang : pack.getConversation(convName).getConfig()
                        .getConfigurationSection("player_options." + name + ".text")
                        .getKeys(false)) {
                    text.put(lang, pack.getString("conversations." + convName
                            + ".player_options." + name + ".text." + lang));
                }
                if (!text.containsKey(defaultLang)) {
                    throw new InstructionParseException(
                            "No default language for " + name + " player option");
                }
            } else {
                text.put(defaultLang, pack.getString("conversations." + convName
                        + ".player_options." + name + ".text"));
            };
            for (String theText : text.values()) {
                if (theText == null || theText.equals(""))
                    throw new InstructionParseException(String.format(
                            "Text not defined in player option %s", name));
            }
            String rawConditions = pack.getString("conversations." + convName
                    + ".player_options." + name + ".conditions");
            if (rawConditions != null && !rawConditions.equals("")) {
                conditions = rawConditions.split(",");
            } else {
                conditions = new String[]{};
            }
            for (int i = 0; i < conditions.length; i++) {
                if (!conditions[i].contains(".")) {
                    conditions[i] = pack.getName() + "." + conditions[i];
                }
            }
            String rawEvents = pack.getString("conversations." + convName
                    + ".player_options." + name + ".events");
            if (rawEvents != null && !rawEvents.equals("")) {
                events = rawEvents.split(",");
            } else {
                events = new String[]{};
            }
            for (int i = 0; i < events.length; i++) {
                if (!events[i].contains(".")) {
                    events[i] = pack.getName() + "." + events[i];
                }
            }
            String rawPointers = pack.getString("conversations." + convName
                    + ".player_options." + name + ".pointer");
            if (rawPointers != null && !rawPointers.equals("")) {
                pointers = rawPointers.split(",");
            } else {
                pointers = new String[]{};
            }
        }
        
        public String getName() {
            return name;
        }
        
        public String getText(String lang) {
            String theText = text.get(lang);
            if (theText == null) {
                theText = text.get(Config.getLanguage());
            }
            return theText;
        }
        
        public String[] getConditions() {
            return conditions;
        }
        
        public String[] getEvents() {
            return events;
        }
        
        public String[] getPointers() {
            return pointers;
        }
    }
    
    public static enum OptionType {
        NPC, PLAYER
    }
    
    public static enum RequestType {
        CONDITION, EVENT, POINTER
    }
}
