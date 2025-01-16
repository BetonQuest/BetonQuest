package org.betonquest.betonquest.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a quest canceler, which cancels quests for players.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.CouplingBetweenObjects"})
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
     * @throws QuestException when parsing, the canceler fails for some reason
     */
    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    public QuestCanceler(final QuestPackage pack, final String cancelerID) throws QuestException {
        this.cancelerID = Utils.getNN(cancelerID, "Name is null");
        this.pack = Utils.getNN(pack, "Package does not exist");
        final ConfigurationSection section = pack.getConfig().getConfigurationSection("cancel." + cancelerID);
        Utils.getNN(section, "Missing Canceler section!");
        // get the name
        if (section.isConfigurationSection("name")) {
            for (final String lang : section.getConfigurationSection("name")
                    .getKeys(false)) {
                name.put(lang, section.getString("name." + lang));
            }
        } else {
            name.put(Config.getLanguage(), section.getString("name"));
        }
        // get the item
        final String itemString = section.getString("item");
        item = itemString == null ? pack.getRawString("items.cancel_button") : itemString;
        // parse it to get the data
        events = parseID(section, "events", EventID::new);
        conditions = parseID(section, "conditions", ConditionID::new);
        objectives = parseID(section, "objectives", ObjectiveID::new);
        tags = split(section, "tags");
        points = split(section, "points");
        journal = split(section, "journal");
        final String rawLoc = GlobalVariableResolver.resolve(pack, section.getString("loc"));
        if (rawLoc != null) {
            Location tmp;
            try {
                tmp = VariableLocation.parse(rawLoc);
            } catch (final QuestException e) {
                log.warn(pack, "Could not parse location in quest canceler '" + name + "': " + e.getMessage(), e);
                tmp = null;
            }
            loc = tmp;
        } else {
            loc = null;
        }
    }

    @Nullable
    private String[] split(final ConfigurationSection section, final String path) {
        final String raw = section.getString(path);
        return raw == null ? null : GlobalVariableResolver.resolve(pack, raw).split(",");
    }

    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    @Nullable
    private <T extends ID> T[] parseID(final ConfigurationSection section, final String path, final IDArgument<T> argument) throws QuestException {
        final String[] rawObjectives = split(section, path);
        if (rawObjectives == null || rawObjectives.length == 0) {
            return null;
        }
        try {
            final T first = argument.convert(pack, rawObjectives[0]);
            @SuppressWarnings("unchecked") final T[] converted = (T[]) Array.newInstance(first.getClass(), rawObjectives.length);
            converted[0] = first;
            for (int i = 1; i < rawObjectives.length; i++) {
                converted[i] = argument.convert(pack, rawObjectives[i]);
            }
            return converted;
        } catch (final ObjectNotFoundException e) {
            throw new QuestException("Error while parsing quest canceler " + path + ": " + e.getMessage(), e);
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
    public void cancel(final OnlineProfile onlineProfile) {
        log.debug("Canceling the quest " + name + " for " + onlineProfile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(onlineProfile);
        // remove tags, points, objectives and journals
        removeSimple(tags, "tag", playerData::removeTag);
        removeSimple(points, "point", playerData::removePointsCategory);
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
            removeSimple(this.journal, "journal entry", journal::removePointer);
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
        } catch (final QuestException exception) {
            log.warn("The notify system was unable to play a sound for the 'quest_canceled' category in quest '" + name + "'. Error was: '" + exception.getMessage() + "'");
        }
    }

    private void removeSimple(@Nullable final String[] toRemove, final String logIdentifier, final Consumer<String> action) {
        if (toRemove != null) {
            for (final String entry : toRemove) {
                log.debug("  Removing " + logIdentifier + " " + entry);
                if (entry.contains(".")) {
                    action.accept(entry);
                } else {
                    action.accept(pack.getQuestPath() + "." + entry);
                }
            }
        }
    }

    /**
     * Returns a name of this quest canceler in the language of the player,
     * default language, English, or if none of the above are specified,
     * "Quest". In that case, it will also log an error to the console.
     *
     * @param profile the {@link Profile} of the player
     * @return the name of the quest canceler
     */
    public String getName(final Profile profile) {
        String questName = name.get(BetonQuest.getInstance().getPlayerDataStorage().get(profile).getLanguage());
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
            } catch (final QuestException | ObjectNotFoundException e) {
                log.warn("Could not load cancel button: " + e.getMessage(), e);
            }
        }
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName(profile));
        stack.setItemMeta(meta);
        return stack;
    }
}
