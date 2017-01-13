/**
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

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import pl.betoncraft.betonquest.config.ConfigAccessor.AccessorType;
import pl.betoncraft.betonquest.utils.Debug;

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
	 */
	public ConfigPackage(File pack, String name) {
		if (!pack.isDirectory())
			return;
		folder = pack;
		this.name = name;
		main = new ConfigAccessor(new File(pack, "main.yml"), "main.yml", AccessorType.MAIN);
		events = new ConfigAccessor(new File(pack, "events.yml"), "events.yml", AccessorType.EVENTS);
		conditions = new ConfigAccessor(new File(pack, "conditions.yml"), "conditions.yml", AccessorType.CONDITIONS);
		objectives = new ConfigAccessor(new File(pack, "objectives.yml"), "objectives.yml", AccessorType.OBJECTIVES);
		journal = new ConfigAccessor(new File(pack, "journal.yml"), "journal.yml", AccessorType.JOURNAL);
		items = new ConfigAccessor(new File(pack, "items.yml"), "items.yml", AccessorType.ITEMS);
		custom = new ConfigAccessor(new File(pack, "custom.yml"), "custom.yml", AccessorType.CUSTOM);
		File convFile = new File(pack, "conversations");
		if (convFile.exists() && convFile.isDirectory()) {
			for (File conv : convFile.listFiles()) {
				String convName = conv.getName();
				if (convName.endsWith(".yml")) {
					ConfigAccessor convAccessor = new ConfigAccessor(conv, convName, AccessorType.CONVERSATION);
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
	 * @param address
	 *            address of the string
	 * @return the raw string
	 */
	public String getRawString(String address) {
		// prepare the address
		String[] parts = address.split("\\.");
		if (parts.length < 2) {
			return null;
		}
		// get the right file
		String file = parts[0];
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
		StringBuilder newPath = new StringBuilder();
		for (int i = startPath; i < parts.length; i++) {
			newPath.append(parts[i]);
			if (i < parts.length - 1) {
				newPath.append('.');
			}
		}
		return config.getConfig().getString(newPath.toString(), null);
	}

	/**
	 * Returns a string with inserted variables
	 * 
	 * @param address
	 *            address of the string
	 * @return the string
	 */
	public String getString(String address) {
		String value = getRawString(address);
		if (value == null) {
			return null;
		}
		if (!value.contains("$")) {
			return value;
		}
		// handle "$this$" variables
		value = value.replace("$this$", name);
		// handle the rest
		while (true) {
			int start = value.indexOf('$');
			int end = value.indexOf('$', start + 1);
			if (start == -1 || end == -1) {
				break;
			}
			String varName = value.substring(start + 1, end);
			String varVal = main.getConfig().getString("variables." + varName);
			if (varVal == null) {
				Debug.error(String.format("Variable %s not defined in package %s", varName, name));
				return null;
			} else if (varVal
					.matches("^\\$[a-zA-Z0-9]+\\$->\\(\\-?\\d+\\.?\\d*;\\-?\\d+\\.?\\d*;\\-?\\d+\\.?\\d*\\)$")) {
				// handle location variables
				// parse the inner location
				String innerVarName = varVal.substring(1, varVal.indexOf('$', 2));
				String innerVarVal = main.getConfig().getString("variables." + innerVarName);
				if (innerVarVal == null) {
					Debug.error(String.format("Location variable %s is not defined, in variable %s, package %s.",
							innerVarName, varName, name));
					return null;
				}
				if (!innerVarVal.matches("^\\-?\\d+;\\-?\\d+;\\-?\\d+;.+$")) {
					Debug.error(
							String.format("Inner variable %s is not valid location, in variable %s, package %s.",
									innerVarName, varName, name));
					return null;
				}
				double x1, y1, z1;
				String rest;
				try {
					int i = innerVarVal.indexOf(';');
					x1 = Double.parseDouble(innerVarVal.substring(0, i));
					int j = innerVarVal.indexOf(';', i + 1);
					y1 = Double.parseDouble(innerVarVal.substring(i + 1, j));
					int k = innerVarVal.indexOf(';', j + 1);
					z1 = Double.parseDouble(innerVarVal.substring(j + 1, k));
					// rest is world + possible other arguments
					rest = innerVarVal.substring(k, innerVarVal.length());
				} catch (NumberFormatException e) {
					Debug.error(String.format(
							"Could not parse coordinates in inner variable %s in variable %s in package %s",
							innerVarName, varName, name));
					return null;
				}
				// parse the vector
				double x2, y2, z2;
				try {
					int s = varVal.indexOf('(');
					int i = varVal.indexOf(';');
					int j = varVal.indexOf(';', i + 1);
					int e = varVal.indexOf(')');
					x2 = Double.parseDouble(varVal.substring(s + 1, i));
					y2 = Double.parseDouble(varVal.substring(i + 1, j));
					z2 = Double.parseDouble(varVal.substring(j + 1, e));
				} catch (NumberFormatException e) {
					Debug.error(String.format("Could not parse vector inlocation variable %s in package %s",
							varName, name));
					return null;
				}
				double x3 = x1 + x2, y3 = y1 + y2, z3 = z1 + z2;
				value = value.replace("$" + varName + "$", String.format("%.2f;%.2f;%.2f%s", x3, y3, z3, rest));
			} else {
				value = value.replace("$" + varName + "$", varVal);
			}
		}
		return value;
	}

	public boolean setString(String address, String value) {
		// prepare the address
		String[] parts = address.split("\\.");
		if (parts.length < 2) {
			return false;
		}
		// get the right file
		String file = parts[0];
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
		StringBuilder newPath = new StringBuilder();
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
	 * @param name
	 *            name of the conversation to search for
	 * @return the conversation config
	 */
	public ConfigAccessor getConversation(String name) {
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
