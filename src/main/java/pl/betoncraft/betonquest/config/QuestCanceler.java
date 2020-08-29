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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Represents a quest canceler, which cancels quests for players.
 *
 * @author Jakub Sapalski
 */
public class QuestCanceler {

    private String item;
    private String[] tags, points, journal;
    private ConditionID[] conditions;
    private EventID[] events;
    private ObjectiveID[] objectives;
    private Location loc;
    private HashMap<String, String> name = new HashMap<>();

    private String packName;
    private String cancelerName;

    /**
     * Creates a new canceler with given name.
     *
     * @param cancelerID ID of the canceler (package.name)
     * @throws InstructionParseException when parsing the canceler fails for some reason
     */
    public QuestCanceler(final String cancelerID) throws InstructionParseException {
        if (cancelerID == null) {
            throw new InstructionParseException("Name is null");
        }
        // get the instruction
        final String[] parts = cancelerID.split("\\.");
        if (parts.length != 2) {
            throw new InstructionParseException("ID is incorrect");
        }
        packName = parts[0];
        cancelerName = parts[1];
        final ConfigPackage pack = Config.getPackages().get(packName);
        if (pack == null) {
            throw new InstructionParseException("Package does not exist");
        }
        final String rawEvents = pack.getString("main.cancel." + cancelerName + ".events");
        // get the name
        if (pack.getMain().getConfig().isConfigurationSection("cancel." + cancelerName + ".name")) {
            for (final String lang : pack.getMain().getConfig().getConfigurationSection("cancel." + cancelerName + ".name")
                    .getKeys(false)) {
                name.put(lang, pack.getString("main.cancel." + cancelerName + ".name." + lang));
            }
        } else {
            name.put(Config.getLanguage(), pack.getString("main.cancel." + cancelerName + ".name"));
        }
        // get the item
        item = pack.getString("main.cancel." + cancelerName + ".item");
        if (item == null) {
            item = Config.getString(packName + ".items.cancel_button");
        }
        // parse it to get the data
        if (rawEvents == null) {
            events = new EventID[0];
        } else {
            final String[] arr = rawEvents.split(",");
            events = new EventID[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    events[i] = new EventID(Config.getPackages().get(packName), arr[i]);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing quest canceler events: " + e.getMessage(), e);
                }
            }
        }
        final String rawConditions = pack.getString("main.cancel." + cancelerName + ".conditions");
        if (rawConditions == null) {
            conditions = new ConditionID[0];
        } else {
            final String[] arr = rawConditions.split(",");
            conditions = new ConditionID[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    conditions[i] = new ConditionID(Config.getPackages().get(packName), arr[i]);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing quest canceler conditions: " + e.getMessage(), e);
                }
            }
        }
        final String rawObjectives = pack.getString("main.cancel." + cancelerName + ".objectives");
        if (rawObjectives == null) {
            objectives = new ObjectiveID[0];
        } else {
            final String[] arr = rawObjectives.split(",");
            objectives = new ObjectiveID[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    objectives[i] = new ObjectiveID(Config.getPackages().get(packName), arr[i]);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing quest canceler objectives: " + e.getMessage(), e);
                }
            }
        }
        final String rawTags = pack.getString("main.cancel." + cancelerName + ".tags");
        final String rawPoints = pack.getString("main.cancel." + cancelerName + ".points");
        final String rawJournal = pack.getString("main.cancel." + cancelerName + ".journal");
        final String rawLoc = pack.getString("main.cancel." + cancelerName + ".loc");
        tags = rawTags == null ? null : rawTags.split(",");
        points = rawPoints == null ? null : rawPoints.split(",");
        journal = rawJournal == null ? null : rawJournal.split(",");
        final String[] locParts = rawLoc == null ? null : rawLoc.split(";");
        // get location
        if (locParts != null) {
            if (locParts.length != 4 && locParts.length != 6) {
                LogUtils.getLogger().log(Level.WARNING, "Wrong location format in quest canceler " + name);
                return;
            }
            final double locX;
            final double locY;
            final double locZ;
            try {
                locX = Double.parseDouble(locParts[0]);
                locY = Double.parseDouble(locParts[1]);
                locZ = Double.parseDouble(locParts[2]);
            } catch (NumberFormatException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not parse location in quest canceler " + name);
                LogUtils.logThrowable(e);
                return;
            }
            final World world = Bukkit.getWorld(locParts[3]);
            if (world == null) {
                LogUtils.getLogger().log(Level.WARNING, "The world doesn't exist in quest canceler " + name);
                return;
            }
            float yaw = 0, pitch = 0;
            if (locParts.length == 6) {
                try {
                    yaw = Float.parseFloat(locParts[4]);
                    pitch = Float.parseFloat(locParts[5]);
                } catch (NumberFormatException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse yaw/pitch in quest canceler " + name + ", setting to 0");
                    LogUtils.logThrowable(e);
                    yaw = 0;
                    pitch = 0;
                }
            }
            loc = new Location(world, locX, locY, locZ, yaw, pitch);
        }
    }

    /**
     * Checks conditions of this canceler to decide if it should be shown to the
     * player or not.
     *
     * @param playerID ID of the player
     * @return true if all conditions are met, false otherwise
     */
    public boolean show(final String playerID) {
        if (conditions == null) {
            return true;
        }
        for (final ConditionID condition : conditions) {
            if (!BetonQuest.condition(playerID, condition)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cancels the quest for specified player.
     *
     * @param playerID ID of the player
     */
    public void cancel(final String playerID) {
        LogUtils.getLogger().log(Level.FINE, "Canceling the quest " + name + " for player " + PlayerConverter.getName(playerID));
        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
        // remove tags, points, objectives and journals
        if (tags != null) {
            for (final String tag : tags) {
                LogUtils.getLogger().log(Level.FINE, "  Removing tag " + tag);
                if (tag.contains(".")) {
                    playerData.removeTag(tag);
                } else {
                    playerData.removeTag(packName + "." + tag);
                }
            }
        }
        if (points != null) {
            for (final String point : points) {
                LogUtils.getLogger().log(Level.FINE, "  Removing points " + point);
                if (point.contains(".")) {
                    playerData.removePointsCategory(point);
                } else {
                    playerData.removePointsCategory(packName + "." + point);
                }
            }
        }
        if (objectives != null) {
            for (final ObjectiveID objectiveID : objectives) {
                LogUtils.getLogger().log(Level.FINE, "  Removing objective " + objectiveID);
                BetonQuest.getInstance().getObjective(objectiveID).removePlayer(playerID);
                playerData.removeRawObjective(objectiveID);
            }
        }
        if (journal != null) {
            final Journal journal = playerData.getJournal();
            for (final String entry : this.journal) {
                LogUtils.getLogger().log(Level.FINE, "  Removing entry " + entry);
                if (entry.contains(".")) {
                    journal.removePointer(entry);
                } else {
                    journal.removePointer(packName + "." + entry);
                }
            }
            journal.update();
        }
        // teleport player to the location
        if (loc != null) {
            LogUtils.getLogger().log(Level.FINE, "  Teleporting to new location");
            PlayerConverter.getPlayer(playerID).teleport(loc);
        }
        // fire all events
        if (events != null) {
            for (final EventID event : events) {
                BetonQuest.event(playerID, event);
            }
        }
        // done
        LogUtils.getLogger().log(Level.FINE, "Quest removed!");
        final String questName = getName(playerID);
        Config.sendNotify(playerID, "quest_canceled", new String[]{questName}, "quest_cancelled,quest_canceled,info");
    }

    /**
     * Returns a name of this quest canceler in the language of the player,
     * default language, English or if none of above are specified, simply
     * "Quest". In that case, it will also log an error to the console.
     *
     * @param playerID ID of the player
     * @return the name of the quest canceler
     */
    public String getName(final String playerID) {
        String questName = name.get(BetonQuest.getInstance().getPlayerData(playerID).getLanguage());
        if (questName == null) {
            questName = name.get(Config.getLanguage());
        }
        if (questName == null) {
            questName = name.get("en");
        }
        if (questName == null) {
            LogUtils.getLogger().log(Level.WARNING, "Default quest name not defined in canceler " + packName + "." + cancelerName);
            questName = "Quest";
        }
        return questName.replace("_", " ").replace("&", "§");
    }

    public ItemStack getItem(final String playerID) {
        ItemStack stack = new ItemStack(Material.BONE);
        if (item != null) {
            try {
                final ItemID itemID = new ItemID(Config.getPackages().get(packName), item);
                stack = new QuestItem(itemID).generate(1);
            } catch (InstructionParseException | ObjectNotFoundException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not load cancel button: " + e.getMessage());
                LogUtils.logThrowable(e);
            }
        }
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName(playerID));
        stack.setItemMeta(meta);
        return stack;
    }

}
