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
    
    private String quester;
    private String unknown;
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
        this.quester = pack.getString("conversations." + name + ".quester");
        this.unknown = pack.getString("conversations." + name + ".unknown");
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
        NPCOptions = new HashMap<>();
        for (String key : NPCSection.getKeys(false)) {
            NPCOptions.put(key, new NPCOption(key));
        }
        // load all Player options
        ConfigurationSection playerSection = pack.getConversation(name)
                .getConfig().getConfigurationSection("player_options");
        playerOptions = new HashMap<>();
        for (String key : playerSection.getKeys(false)) {
            playerOptions.put(key, new PlayerOption(key));
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
     * @return the quester's name
     */
    public String getQuester() {
        return quester;
    }
    
    /**
     * @return the unknown message
     */
    public String getUnknown() {
        return unknown;
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
    
    public String getText(String option, OptionType type) {
        if (type == OptionType.NPC) {
            return NPCOptions.get(option).getText();
        } else {
            return playerOptions.get(option).getText();
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
        public String getText();
        public String[] getConditions();
        public String[] getEvents();
        public String[] getPointers();
    }

    /**
     * Represents an option which can be said by the NPC
     */
    private class NPCOption implements Option {
        
        private String name;
        
        private String text;
        private String[] conditions;
        private String[] events;
        private String[] pointers;
        
        public NPCOption(String name) throws InstructionParseException {
            this.name = name;
            text = pack.getString("conversations." + convName + ".NPC_options." + name + ".text");
            if (text == null || text.equals("")) throw new InstructionParseException(String.format(
                    "Text not defined in NPC option %s", name));
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
        
        public String getText() {
            return text;
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
        
        private String text;
        private String[] conditions;
        private String[] events;
        private String[] pointers;
        
        public PlayerOption(String name) throws InstructionParseException {
            this.name = name;
            text = pack.getString("conversations." + convName + ".player_options." + name + ".text");
            if (text == null || text.equals("")) throw new InstructionParseException(String.format(
                    "Text not defined in player option %s", name));
            String rawConditions = pack.getString("conversations." + convName + ".player_options." + name + ".conditions");
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
            String rawEvents = pack.getString("conversations." + convName + ".player_options." + name + ".events");
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
            String rawPointers = pack.getString("conversations." + convName + ".player_options." + name + ".pointer");
            if (rawPointers != null && !rawPointers.equals("")) {
                pointers = rawPointers.split(",");
            } else {
                pointers = new String[]{};
            }
        }
        
        public String getName() {
            return name;
        }
        
        public String getText() {
            return text;
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
