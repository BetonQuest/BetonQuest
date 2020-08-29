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
package pl.betoncraft.betonquest.config;

import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.GlobalVariableID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds configuration files of the package
 *
 * @author Jakub Sapalski
 */
public class ConfigPackage {

    private String name;
    private File folder;
    private boolean enabled;

    private ConfigAccessor main;
    private ConfigAccessor events;
    private ConfigAccessor conditions;
    private ConfigAccessor objectives;
    private ConfigAccessor journal;
    private ConfigAccessor items;
    private ConfigAccessor custom;
    private HashMap<String, ConfigAccessor> conversations = new HashMap<>();

    /**
     * Loads a package from specified directory. It doesn't have to be valid
     * package directory.
     *
     * @param pack the directory containing this package
     * @param name the name of this package
     */
    public ConfigPackage(final File pack, final String name) {
        if (!pack.isDirectory()) {
            return;
        }
        folder = pack;
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
     * @return if the package is enabled (true) or disabled (false)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns a raw string (without inserted variables)
     *
     * @param address address of the string
     * @return the raw string
     */
    public String getRawString(final String address) {
        // prepare the address
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        // get the right file
        final String file = parts[0];
        ConfigAccessor config = null;
        int startPath = 1;
        switch (file) {
            case "main":
                config = main;
                break;
            case "events":
                config = events;
                break;
            case "conditions":
                config = conditions;
                break;
            case "journal":
                config = journal;
                break;
            case "items":
                config = items;
                break;
            case "objectives":
                config = objectives;
                break;
            case "conversations":
                // conversations go one level deeper
                if (parts.length < 3) {
                    return null;
                }
                config = conversations.get(parts[1]);
                startPath = 2;
                break;
            default:
                break;
        }
        // if config accessor wasn't found, return null
        if (config == null) {
            return null;
        }
        // retrieve the string from the path
        final StringBuilder newPath = new StringBuilder();
        for (int i = startPath; i < parts.length; i++) {
            newPath.append(parts[i]);
            if (i < parts.length - 1) {
                newPath.append('.');
            }
        }
        return config.getConfig().getString(newPath.toString(), null);
    }

    /**
     * Perform Variable substitution
     */
    public String subst(String input) {
        if (input == null) {
            return null;
        }

        // handle "$this$" variables
        input = input.replace("$this$", name);

        // handle the rest
        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        while (true) {
            final Matcher matcher = globalVariableRegex.matcher(input);
            if (!matcher.find()) {
                break;
            }
            final String varName = matcher.group(1);
            final String varVal;
            try {
                final GlobalVariableID variableID = new GlobalVariableID(this, varName);
                varVal = variableID.getPackage().getMain().getConfig().getString("variables." + variableID.getBaseID());
            } catch (ObjectNotFoundException e) {
                LogUtils.getLogger().log(Level.WARNING, e.getMessage());
                LogUtils.logThrowable(e);
                return input;
            }
            if (varVal == null) {
                LogUtils.getLogger().log(Level.WARNING, String.format("Variable %s not defined in package %s", varName, name));
                return input;
            }

            if (varVal
                    .matches("^\\$[a-zA-Z0-9]+\\$->\\(\\-?\\d+\\.?\\d*;\\-?\\d+\\.?\\d*;\\-?\\d+\\.?\\d*\\)$")) {
                // handle location variables
                // parse the inner location
                final String innerVarName = varVal.substring(1, varVal.indexOf('$', 2));
                final String innerVarVal = main.getConfig().getString("variables." + innerVarName);
                if (innerVarVal == null) {
                    LogUtils.getLogger().log(Level.WARNING, String.format("Location variable %s is not defined, in variable %s, package %s.",
                            innerVarName, varName, name));
                    return input;
                }

                if (!innerVarVal.matches("^\\-?\\d+;\\-?\\d+;\\-?\\d+;.+$")) {
                    LogUtils.getLogger().log(Level.WARNING,
                            String.format("Inner variable %s is not valid location, in variable %s, package %s.",
                                    innerVarName, varName, name));
                    return input;
                }

                final double locX;
                final double locY;
                final double locZ;
                final String rest;
                try {
                    final int offset1 = innerVarVal.indexOf(';');
                    locX = Double.parseDouble(innerVarVal.substring(0, offset1));
                    final int offset2 = innerVarVal.indexOf(';', offset1 + 1);
                    locY = Double.parseDouble(innerVarVal.substring(offset1 + 1, offset2));
                    final int offset3 = innerVarVal.indexOf(';', offset2 + 1);
                    locZ = Double.parseDouble(innerVarVal.substring(offset2 + 1, offset3));
                    // rest is world + possible other arguments
                    rest = innerVarVal.substring(offset3);
                } catch (NumberFormatException e) {
                    LogUtils.getLogger().log(Level.WARNING, String.format(
                            "Could not parse coordinates in inner variable %s in variable %s in package %s",
                            innerVarName, varName, name));
                    LogUtils.logThrowable(e);
                    return input;
                }
                // parse the vector
                final double vecLocX;
                final double vecLocY;
                final double vecLocZ;
                try {
                    final int offset1 = varVal.indexOf('(');
                    final int offset2 = varVal.indexOf(';');
                    final int offset3 = varVal.indexOf(';', offset2 + 1);
                    final int offset4 = varVal.indexOf(')');
                    vecLocX = Double.parseDouble(varVal.substring(offset1 + 1, offset2));
                    vecLocY = Double.parseDouble(varVal.substring(offset2 + 1, offset3));
                    vecLocZ = Double.parseDouble(varVal.substring(offset3 + 1, offset4));
                } catch (NumberFormatException e) {
                    LogUtils.getLogger().log(Level.WARNING, String.format("Could not parse vector inlocation variable %s in package %s",
                            varName, name));
                    LogUtils.logThrowable(e);
                    return input;
                }
                final double locationX = locX + vecLocX;
                final double locationY = locY + vecLocY;
                final double locationZ = locZ + vecLocZ;
                input = input.replace("$" + varName + "$", String.format(Locale.US, "%.2f;%.2f;%.2f%s", locationX, locationY, locationZ, rest));
            } else {
                input = input.replace("$" + varName + "$", varVal);
            }
        }

        return input;
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

        return subst(value);
    }

    /**
     * Returns a string with inserted variables and color codes and linebreaks replaced
     *
     * @param address
     * @return a fully formatted string
     */
    public String getFormattedString(final String address) {
        return Utils.format(getString(address));
    }

    @SuppressWarnings("PMD.LinguisticNaming")
    public boolean setString(final String address, final String value) {
        // prepare the address
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        // get the right file
        final String file = parts[0];
        ConfigAccessor config = null;
        int startPath = 1;
        switch (file) {
            case "main":
                config = main;
                break;
            case "events":
                config = events;
                break;
            case "conditions":
                config = conditions;
                break;
            case "journal":
                config = journal;
                break;
            case "items":
                config = items;
                break;
            case "objectives":
                config = objectives;
                break;
            case "conversations":
                // conversations go one level deeper
                if (parts.length < 3) {
                    return false;
                }
                config = conversations.get(parts[1]);
                startPath = 2;
                break;
            default:
                break;
        }
        // if config accessor wasn't found, return false
        if (config == null) {
            return false;
        }
        // retrieve the string from the path
        final StringBuilder newPath = new StringBuilder();
        for (int i = startPath; i < parts.length; i++) {
            newPath.append(parts[i]);
            if (i < parts.length - 1) {
                newPath.append('.');
            }
        }
        config.getConfig().set(newPath.toString(), value);
        config.saveConfig();
        return true;
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

    /**
     * @return the name of this package
     */
    public String getName() {
        return name;
    }

    /**
     * @return the folder which contains this package
     */
    public File getFolder() {
        return folder;
    }

}
