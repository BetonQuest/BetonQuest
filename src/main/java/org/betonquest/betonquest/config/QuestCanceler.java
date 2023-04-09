package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.item.QuestItem;
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
public class QuestCanceler {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(QuestCanceler.class);

    private final String[] tags;
    private final String[] points;
    private final String[] journal;
    private final ConditionID[] conditions;
    private final EventID[] events;
    private final ObjectiveID[] objectives;
    private final Map<String, String> name = new HashMap<>();
    private final QuestPackage pack;
    private final String cancelerID;
    private final String item;
    private final Location loc;

    /**
     * Creates a new canceler with given name.
     *
     * @param cancelerID ID of the canceler (package.name)
     * @throws InstructionParseException when parsing the canceler fails for some reason
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public QuestCanceler(final QuestPackage pack, final String cancelerID) throws InstructionParseException {
        if (cancelerID == null) {
            throw new InstructionParseException("Name is null");
        }
        if (pack == null) {
            throw new InstructionParseException("Package does not exist");
        }
        this.pack = pack;
        this.cancelerID = cancelerID;
        final String rawEvents = pack.getString("cancel." + cancelerID + ".events");
        // get the name
        if (pack.getConfig().isConfigurationSection("cancel." + cancelerID + ".name")) {
            for (final String lang : pack.getConfig().getConfigurationSection("cancel." + cancelerID + ".name")
                    .getKeys(false)) {
                name.put(lang, pack.getString("cancel." + cancelerID + ".name." + lang));
            }
        } else {
            name.put(Config.getLanguage(), pack.getString("cancel." + cancelerID + ".name"));
        }
        // get the item
        final String itemString = pack.getString("cancel." + cancelerID + ".item");
        item = itemString == null ? Config.getString(pack.getQuestPath() + ".items.cancel_button") : itemString;
        // parse it to get the data
        if (rawEvents == null) {
            events = new EventID[0];
        } else {
            final String[] arr = rawEvents.split(",");
            events = new EventID[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    events[i] = new EventID(pack, arr[i]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing quest canceler events: " + e.getMessage(), e);
                }
            }
        }
        final String rawConditions = pack.getString("cancel." + cancelerID + ".conditions");
        if (rawConditions == null) {
            conditions = new ConditionID[0];
        } else {
            final String[] arr = rawConditions.split(",");
            conditions = new ConditionID[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    conditions[i] = new ConditionID(pack, arr[i]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing quest canceler conditions: " + e.getMessage(), e);
                }
            }
        }
        final String rawObjectives = pack.getString("cancel." + cancelerID + ".objectives");
        if (rawObjectives == null) {
            objectives = new ObjectiveID[0];
        } else {
            final String[] arr = rawObjectives.split(",");
            objectives = new ObjectiveID[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    objectives[i] = new ObjectiveID(pack, arr[i]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing quest canceler objectives: " + e.getMessage(), e);
                }
            }
        }
        final String rawTags = pack.getString("cancel." + cancelerID + ".tags");
        final String rawPoints = pack.getString("cancel." + cancelerID + ".points");
        final String rawJournal = pack.getString("cancel." + cancelerID + ".journal");
        final String rawLoc = pack.getString("cancel." + cancelerID + ".loc");
        tags = rawTags == null ? null : rawTags.split(",");
        points = rawPoints == null ? null : rawPoints.split(",");
        journal = rawJournal == null ? null : rawJournal.split(",");
        final String[] locParts = rawLoc == null ? null : rawLoc.split(";");
        // get location
        if (locParts != null) {
            if (locParts.length != 4 && locParts.length != 6) {
                LOG.warn(pack, "Wrong location format in quest canceler " + name);
                loc = null;
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
                LOG.warn(pack, "Could not parse location in quest canceler " + name, e);
                loc = null;
                return;
            }
            final World world = Bukkit.getWorld(locParts[3]);
            if (world == null) {
                LOG.warn(pack, "The world doesn't exist in quest canceler " + name);
                loc = null;
                return;
            }
            float yaw = 0;
            float pitch = 0;
            if (locParts.length == 6) {
                try {
                    yaw = Float.parseFloat(locParts[4]);
                    pitch = Float.parseFloat(locParts[5]);
                } catch (final NumberFormatException e) {
                    LOG.warn(pack, "Could not parse yaw/pitch in quest canceler " + name + ", setting to 0", e);
                    yaw = 0;
                    pitch = 0;
                }
            }
            loc = new Location(world, locX, locY, locZ, yaw, pitch);
        } else {
            loc = null;
        }
    }

    /**
     * Checks conditions of this canceler to decide if it should be shown to the
     * player or not.
     *
     * @param profile the {@link Profile} of the player
     * @return true if all conditions are met, false otherwise
     */
    public boolean show(final Profile profile) {
        if (conditions == null) {
            return true;
        }
        return BetonQuest.conditions(profile, conditions);
    }

    /**
     * Cancels the quest for specified player.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    public void cancel(final OnlineProfile onlineProfile) {
        LOG.debug("Canceling the quest " + name + " for " + onlineProfile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
        // remove tags, points, objectives and journals
        if (tags != null) {
            for (final String tag : tags) {
                LOG.debug("  Removing tag " + tag);
                if (tag.contains(".")) {
                    playerData.removeTag(tag);
                } else {
                    playerData.removeTag(pack.getQuestPath() + "." + tag);
                }
            }
        }
        if (points != null) {
            for (final String point : points) {
                LOG.debug("  Removing points " + point);
                if (point.contains(".")) {
                    playerData.removePointsCategory(point);
                } else {
                    playerData.removePointsCategory(pack.getQuestPath() + "." + point);
                }
            }
        }
        if (objectives != null) {
            for (final ObjectiveID objectiveID : objectives) {
                LOG.debug(objectiveID.getPackage(), "  Removing objective " + objectiveID);
                final Objective objective = BetonQuest.getInstance().getObjective(objectiveID);
                objective.cancelObjectiveForPlayer(onlineProfile);
                playerData.removeRawObjective(objectiveID);
            }
        }
        if (journal != null) {
            final Journal journal = playerData.getJournal();
            for (final String entry : this.journal) {
                LOG.debug("  Removing entry " + entry);
                if (entry.contains(".")) {
                    journal.removePointer(entry);
                } else {
                    journal.removePointer(pack.getQuestPath() + "." + entry);
                }
            }
            journal.update();
        }
        // teleport player to the location
        if (loc != null) {
            LOG.debug("  Teleporting to new location");
            onlineProfile.getPlayer().teleport(loc);
        }
        // fire all events
        if (events != null) {
            for (final EventID event : events) {
                BetonQuest.event(onlineProfile, event);
            }
        }
        // done
        LOG.debug("Quest removed!");
        final String questName = getName(onlineProfile);
        try {
            Config.sendNotify(pack.getQuestPath(), onlineProfile, "quest_canceled", new String[]{questName}, "quest_cancelled,quest_canceled,info");
        } catch (final QuestRuntimeException exception) {
            LOG.warn("The notify system was unable to play a sound for the 'quest_canceled' category in quest '" + name + "'. Error was: '" + exception.getMessage() + "'");
        }
    }

    /**
     * Returns a name of this quest canceler in the language of the player,
     * default language, English or if none of above are specified, simply
     * "Quest". In that case, it will also log an error to the console.
     *
     * @param profile the {@link Profile} of the player
     * @return the name of the quest canceler
     */
    public String getName(final Profile profile) {
        String questName = name.get(BetonQuest.getInstance().getPlayerData(profile).getLanguage());
        if (questName == null) {
            questName = name.get(Config.getLanguage());
        }
        if (questName == null) {
            questName = name.get("en");
        }
        if (questName == null) {
            LOG.warn("Quest name is not defined in canceler " + pack.getQuestPath() + "." + cancelerID);
            questName = "Quest";
        }
        return questName.replace("_", " ").replace("&", "§");
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public ItemStack getItem(final Profile profile) {
        ItemStack stack = new ItemStack(Material.BONE);
        if (item != null) {
            try {
                final ItemID itemID = new ItemID(pack, item);
                stack = new QuestItem(itemID).generate(1);
            } catch (final InstructionParseException | ObjectNotFoundException e) {
                LOG.warn("Could not load cancel button: " + e.getMessage(), e);
            }
        }
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName(profile));
        stack.setItemMeta(meta);
        return stack;
    }

}
