package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a quest canceler, which cancels quests for players.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition"})
@CustomLog
public class QuestCanceler {

    private final String[] tags;
    private final String[] points;
    private final String[] journal;
    private final ConditionID[] conditions;
    private final EventID[] events;
    private final ObjectiveID[] objectives;
    private final Map<String, String> name = new HashMap<>();
    private final String packName;
    private final String cancelerName;
    private String item;
    private Location loc;

    /**
     * Creates a new canceler with given name.
     *
     * @param cancelerID ID of the canceler (package.name)
     * @throws InstructionParseException when parsing the canceler fails for some reason
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
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
                } catch (final ObjectNotFoundException e) {
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
                } catch (final ObjectNotFoundException e) {
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
                } catch (final ObjectNotFoundException e) {
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
                LOG.warning(pack, "Wrong location format in quest canceler " + name);
                return;
            }
            final double locX;
            final double locY;
            final double locZ;
            try {
                locX = Double.parseDouble(locParts[0]);
                locY = Double.parseDouble(locParts[1]);
                locZ = Double.parseDouble(locParts[2]);
            } catch (final NumberFormatException e) {
                LOG.warning(pack, "Could not parse location in quest canceler " + name, e);
                return;
            }
            final World world = Bukkit.getWorld(locParts[3]);
            if (world == null) {
                LOG.warning(pack, "The world doesn't exist in quest canceler " + name);
                return;
            }
            float yaw = 0;
            float pitch = 0;
            if (locParts.length == 6) {
                try {
                    yaw = Float.parseFloat(locParts[4]);
                    pitch = Float.parseFloat(locParts[5]);
                } catch (final NumberFormatException e) {
                    LOG.warning(pack, "Could not parse yaw/pitch in quest canceler " + name + ", setting to 0", e);
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
        return BetonQuest.conditions(playerID, conditions);
    }

    /**
     * Cancels the quest for specified player.
     *
     * @param playerID ID of the player
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public void cancel(final String playerID) {
        LOG.debug(null, "Canceling the quest " + name + " for player " + PlayerConverter.getName(playerID));
        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
        // remove tags, points, objectives and journals
        if (tags != null) {
            for (final String tag : tags) {
                LOG.debug(null, "  Removing tag " + tag);
                if (tag.contains(".")) {
                    playerData.removeTag(tag);
                } else {
                    playerData.removeTag(packName + "." + tag);
                }
            }
        }
        if (points != null) {
            for (final String point : points) {
                LOG.debug(null, "  Removing points " + point);
                if (point.contains(".")) {
                    playerData.removePointsCategory(point);
                } else {
                    playerData.removePointsCategory(packName + "." + point);
                }
            }
        }
        if (objectives != null) {
            for (final ObjectiveID objectiveID : objectives) {
                LOG.debug(objectiveID.getPackage(), "  Removing objective " + objectiveID);
                BetonQuest.getInstance().getObjective(objectiveID).removePlayer(playerID);
                playerData.removeRawObjective(objectiveID);
            }
        }
        if (journal != null) {
            final Journal journal = playerData.getJournal();
            for (final String entry : this.journal) {
                LOG.debug(null, "  Removing entry " + entry);
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
            LOG.debug(null, "  Teleporting to new location");
            PlayerConverter.getPlayer(playerID).teleport(loc);
        }
        // fire all events
        if (events != null) {
            for (final EventID event : events) {
                BetonQuest.event(playerID, event);
            }
        }
        // done
        LOG.debug(null, "Quest removed!");
        final String questName = getName(playerID);
        try {
            Config.sendNotify(packName, playerID, "quest_canceled", new String[]{questName}, "quest_cancelled,quest_canceled,info");
        } catch (final QuestRuntimeException exception) {
            LOG.warning(null, "The notify system was unable to play a sound for the 'quest_canceled' category in quest '" + name + "'. Error was: '" + exception.getMessage() + "'");
        }
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
            LOG.warning(null, "Default quest name not defined in canceler " + packName + "." + cancelerName);
            questName = "Quest";
        }
        return questName.replace("_", " ").replace("&", "§");
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public ItemStack getItem(final String playerID) {
        ItemStack stack = new ItemStack(Material.BONE);
        if (item != null) {
            try {
                final ItemID itemID = new ItemID(Config.getPackages().get(packName), item);
                stack = new QuestItem(itemID).generate(1);
            } catch (InstructionParseException | ObjectNotFoundException e) {
                LOG.warning(null, "Could not load cancel button: " + e.getMessage(), e);
            }
        }
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName(playerID));
        stack.setItemMeta(meta);
        return stack;
    }

}
