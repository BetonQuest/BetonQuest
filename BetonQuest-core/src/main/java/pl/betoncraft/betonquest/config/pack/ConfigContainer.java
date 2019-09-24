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
package pl.betoncraft.betonquest.config.pack;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.betoncraft.betonquest.config.pack.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.GlobalVariableID;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Holds configuration files of the package
 *
 * @author Jakub Sapalski
 */
public class ConfigContainer {

    /**
     * The file of the package
     */
    private File pack;
    /**
     * The name of the Package
     */
    private String name;
    /**
     * Is the package enabled
     */
    private boolean enabled;

    /**
     * The main {@link ConfigAccessor}
     */
    private ConfigAccessor main;
    /**
     * The events {@link ConfigAccessor}
     */
    private ConfigAccessor events;
    /**
     * The conditions {@link ConfigAccessor}
     */
    private ConfigAccessor conditions;
    /**
     * The objectives {@link ConfigAccessor}
     */
    private ConfigAccessor objectives;
    /**
     * The journal {@link ConfigAccessor}
     */
    private ConfigAccessor journal;
    /**
     * The items {@link ConfigAccessor}
     */
    private ConfigAccessor items;
    /**
     * The custom {@link ConfigAccessor}
     */
    private ConfigAccessor custom;
    /**
     * A map of conversations {@link ConfigAccessor}
     */
    private HashMap<String, ConfigAccessor> conversations = new HashMap<>();

    /**
     * Loads a package from specified directory. It doesn't have to be valid package
     * directory.
     *
     * @param pack the directory containing this package
     * @param name the name of this package
     */
    public ConfigContainer(final File pack, final String name) {
        if (!pack.isDirectory()) {
            return;
        }
        this.pack = pack;
        this.name = name;
        main = new ConfigAccessor(new File(pack, "main.yml"), "main.yml", AccessorType.MAIN);
        events = new ConfigAccessor(new File(pack, "events.yml"), "events.yml", AccessorType.EVENTS);
        conditions = new ConfigAccessor(new File(pack, "conditions.yml"), "conditions.yml", AccessorType.CONDITIONS);
        objectives = new ConfigAccessor(new File(pack, "objectives.yml"), "objectives.yml", AccessorType.OBJECTIVES);
        journal = new ConfigAccessor(new File(pack, "journal.yml"), "journal.yml", AccessorType.JOURNAL);
        items = new ConfigAccessor(new File(pack, "items.yml"), "items.yml", AccessorType.ITEMS);
        custom = new ConfigAccessor(new File(pack, "custom.yml"), "custom.yml", AccessorType.CUSTOM);
        final File convFile = new File(pack, "conversations");
        if (convFile.exists() && convFile.isDirectory()) {
            for (final File conv : convFile.listFiles()) {
                final String convName = conv.getName();
                if (convName.endsWith(".yml")) {
                    final ConfigAccessor convAccessor = new ConfigAccessor(conv, convName, AccessorType.CONVERSATION);
                    conversations.put(convName.substring(0, convName.length() - 4), convAccessor);
                }
            }
        }
        enabled = main.getConfig().getBoolean("enabled", true);
    }

    /**
     * @return the folder which contains this package
     */
    public File getPack() {
        return pack;
    }

    /**
     * @return the name of this package
     */
    public String getName() {
        return name;
    }

    /**
     * @return if the package is enabled (true) or disabled (false)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Perform Variable substitution
     */
    public String substitution(final String input) {
        if (input == null) {
            return null;
        }

        String variableString = input.replace("$this$", name);

        // handle the rest
        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        while (true) {
            final Matcher matcher = globalVariableRegex.matcher(variableString);
            if (!matcher.find()) {
                break;
            }
            final String varName = matcher.group(1);
            String varVal;
            try {
                final GlobalVariableID variableID = new GlobalVariableID(this, varName);
                varVal = variableID.getPackage().getMain().getConfig().getString("variables." + variableID.getBaseID());
            } catch (ObjectNotFoundException e) {
                LogUtils.getLogger().log(Level.WARNING, e.getMessage());
                LogUtils.logThrowable(e);
                return variableString;
            }
            if (varVal == null) {
                LogUtils.getLogger().log(Level.WARNING, String.format("Variable %s not defined in package %s", varName, name));
                return variableString;
            }

            if (varVal.matches("^\\$[a-zA-Z0-9]+\\$->" + LocationData.REGEX_VECTOR + "$")) {
                final String innerVarName = varVal.substring(1, varVal.indexOf('$', 2));
                final String innerVarVal = main.getConfig().getString("variables." + innerVarName);
                if (innerVarVal == null) {
                    LogUtils.getLogger().log(Level.WARNING, String.format("Location variable %s is not defined, in variable %s, package %s.",
                            innerVarName, varName, name));
                    return variableString;
                }
                
                LocationData data;
                try {
                    data = new LocationData(name, innerVarVal + "->" + varVal.substring(varVal.indexOf('('), varVal.length()));
                    variableString = variableString.replace("$" + varName + "$", data.toString(null));
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse location! " + e.getMessage());
                    return variableString;
                }catch (QuestRuntimeException e) {
                    LogUtils.getLogger().log(Level.WARNING, "There was a unexpected exception while parsing location! " + e.getMessage());
                    return variableString;
                }

            } else {
                variableString = variableString.replace("$" + varName + "$", varVal);
            }
        }

        return variableString;
    }

    /**
     * Returns a raw string (without inserted variables)
     *
     * @param address address of the string
     * @return the raw string
     */
    public String getRawString(final String address) {
        final ConfigAccessor config = getConfig(address);
        final String path = resolveAddress(address);
        if (config == null || path == null) {
            return null;
        }
        return config.getConfig().getString(path, null);
    }

    /**
     * Return a string with inserted variables. Default of null
     *
     * @param address address of the string
     * @return the string
     */
    public String getString(final String address) {
        return getString(address, null);
    }

    /**
     * Returns a string with inserted variables
     *
     * @param address address of the string
     * @param def     default value if not found
     * @return the string
     */
    public String getString(final String address, final String def) {
        final String value = getRawString(address);
        if (value == null) {
            return def;
        }
        if (!value.contains("$")) {
            return value;
        }

        return substitution(value);
    }

    /**
     * Returns a string with inserted variables and color codes and linebreaks
     * replaced
     *
     * @param address the address to the string
     * @return the formatted string
     */
    public String getFormattedString(final String address) {
        return Utils.format(getString(address));
    }

    /**
     * Set a string in a config
     * 
     * @param address the address to the string
     * @param value   The value
     * @return true, if the string was set
     */
    public boolean setString(final String address, final String value) {
        final ConfigAccessor config = getConfig(address);
        final String path = resolveAddress(address);
        if (config == null || path == null) {
            return false;
        }
        config.getConfig().set(path, value);
        config.saveConfig();
        return true;
    }

    /**
     * Get the {@link ConfigAccessor} by a address
     * 
     * @param address the address of the {@link ConfigAccessor}
     * @return The {@link ConfigAccessor} or null if not found
     */
    private ConfigAccessor getConfig(final String address) {
        final String[] parts = prepareAddress(address);
        if (parts == null) {
            return null;
        }
        return getConfigAccessor(parts[0], parts[1]);
    }

    /**
     * Resolve an address for a config
     * 
     * @param address The address, that should be resolved
     * @return the resolved address
     */
    private String resolveAddress(final String address) {
        final String[] parts = prepareAddress(address);
        if (parts == null) {
            return null;
        }
        final int startPath = parts[0].equals("conversations") && parts.length > 2 ? 2 : 1;
        final StringBuilder newPath = new StringBuilder();
        for (int i = startPath; i < parts.length; i++) {
            newPath.append(parts[i]);
            if (i < parts.length - 1) {
                newPath.append('.');
            }
        }
        return newPath.toString();
    }

    /**
     * Split a address in to parts
     * 
     * @param address The address
     * @return a splitted address
     */
    private String[] prepareAddress(final String address) {
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        return parts;
    }

    /**
     * Get the {@link ConfigAccessor} by the name
     * 
     * @param file              The name of the {@link ConfigAccessor}
     * @param conversationsFile The name of the conversation, if the file equals
     *                          conversations
     * @return the associated ConfigAccessor
     */
    private ConfigAccessor getConfigAccessor(final String file, final String conversationsFile) {
        switch (file) {
        case "main":
            return main;
        case "events":
            return events;
        case "conditions":
            return conditions;
        case "journal":
            return journal;
        case "items":
            return items;
        case "objectives":
            return objectives;
        case "conversations":
            return conversations.get(conversationsFile);
        default:
            return null;
        }
    }

    /**
     * @return the main configuration of the package
     */
    public ConfigAccessor getMain() {
        return main;
    }

    /**
     * @return the events config
     */
    public ConfigAccessor getEvents() {
        return events;
    }

    /**
     * @return the conditions config
     */
    public ConfigAccessor getConditions() {
        return conditions;
    }

    /**
     * @return the journal config
     */
    public ConfigAccessor getJournal() {
        return journal;
    }

    /**
     * @return the items config
     */
    public ConfigAccessor getItems() {
        return items;
    }

    /**
     * @return the objectives config
     */
    public ConfigAccessor getObjectives() {
        return objectives;
    }

    /**
     * @return the config with custom settings
     */
    public ConfigAccessor getCustom() {
        return custom;
    }

    /**
     * @param name name of the conversation to search for
     * @return the conversation config
     */
    public ConfigAccessor getConversation(final String name) {
        return conversations.get(name);
    }

    /**
     * @return the set of conversation names
     */
    public Set<String> getConversationNames() {
        return conversations.keySet();
    }
}