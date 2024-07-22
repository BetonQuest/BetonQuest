package org.betonquest.betonquest.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
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
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

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
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    @Nullable
    private final String[] tags;

    @Nullable
    private final String[] points;

    @Nullable
    private final String[] journal;

    @Nullable
    private final ConditionID[] conditions;

    @Nullable
    private final EventID[] events;

    @Nullable
    private final ObjectiveID[] objectives;

    private final Map<String, String> name = new HashMap<>();

    private final QuestPackage pack;

    private final String cancelerID;

    @Nullable
    private final String item;

    @Nullable
    private final Location loc;

    /**
     * Creates a new canceler with given name.
     *
     * @param pack       the {@link QuestPackage} of the canceler
     * @param cancelerID ID of the canceler (package.name)
     * @throws InstructionParseException when parsing the canceler fails for some reason
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    public QuestCanceler(final QuestPackage pack, final String cancelerID) throws InstructionParseException {
        this.cancelerID = Utils.getNN(cancelerID, "Name is null");
        this.pack = Utils.getNN(pack, "Package does not exist");
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
        item = itemString == null ? pack.getRawString("items.cancel_button") : itemString;
        // parse it to get the data
        if (rawEvents == null) {
            events = null;
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
            conditions = null;
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
            objectives = null;
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
                log.warn(pack, "Wrong location format in quest canceler " + name);
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
                log.warn(pack, "Could not parse location in quest canceler " + name, e);
                loc = null;
                return;
            }
            final World world = Bukkit.getWorld(locParts[3]);
            if (world == null) {
                log.warn(pack, "The world doesn't exist in quest canceler " + name);
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
                    log.warn(pack, "Could not parse yaw/pitch in quest canceler " + name + ", setting to 0", e);
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
        return conditions == null || BetonQuest.conditions(profile, conditions);
    }

    /**
     * Cancels the quest for specified player.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    public void cancel(final OnlineProfile onlineProfile) {
        log.debug("Canceling the quest " + name + " for " + onlineProfile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
        // remove tags, points, objectives and journals
        if (tags != null) {
            for (final String tag : tags) {
                log.debug("  Removing tag " + tag);
                if (tag.contains(".")) {
                    playerData.removeTag(tag);
                } else {
                    playerData.removeTag(pack.getQuestPath() + "." + tag);
                }
            }
        }
        if (points != null) {
            for (final String point : points) {
                log.debug("  Removing points " + point);
                if (point.contains(".")) {
                    playerData.removePointsCategory(point);
                } else {
                    playerData.removePointsCategory(pack.getQuestPath() + "." + point);
                }
            }
        }
        if (objectives != null) {
            for (final ObjectiveID objectiveID : objectives) {
                log.debug(objectiveID.getPackage(), "  Removing objective " + objectiveID);
                final Objective objective = BetonQuest.getInstance().getObjective(objectiveID);
                if (objective == null) {
                    log.warn("Could not find objective " + objectiveID + " in QuestCanceler " + name);
                } else {
                    objective.cancelObjectiveForPlayer(onlineProfile);
                }
                playerData.removeRawObjective(objectiveID);
            }
        }
        if (journal != null) {
            final Journal journal = playerData.getJournal();
            for (final String entry : this.journal) {
                log.debug("  Removing entry " + entry);
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
            log.debug("  Teleporting to new location");
            onlineProfile.getPlayer().teleport(loc);
        }
        // fire all events
        if (events != null) {
            for (final EventID event : events) {
                BetonQuest.event(onlineProfile, event);
            }
        }
        // done
        log.debug("Quest removed!");
        final String questName = getName(onlineProfile);
        try {
            Config.sendNotify(pack, onlineProfile, "quest_canceled", new String[]{questName}, "quest_cancelled,quest_canceled,info");
        } catch (final QuestRuntimeException exception) {
            log.warn("The notify system was unable to play a sound for the 'quest_canceled' category in quest '" + name + "'. Error was: '" + exception.getMessage() + "'");
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
            log.warn("Quest name is not defined in canceler " + pack.getQuestPath() + "." + cancelerID);
            questName = "Quest";
        }
        return questName.replace("_", " ").replace("&", "§");
    }

    public ItemStack getItem(final Profile profile) {
        ItemStack stack = new ItemStack(Material.BONE);
        if (item != null) {
            try {
                final ItemID itemID = new ItemID(pack, item);
                stack = new QuestItem(itemID).generate(1);
            } catch (final InstructionParseException | ObjectNotFoundException e) {
                log.warn("Could not load cancel button: " + e.getMessage(), e);
            }
        }
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName(profile));
        stack.setItemMeta(meta);
        return stack;
    }
}
