package org.betonquest.betonquest.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.notify.Notify;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a quest canceler, which cancels quests for players.
 */
public class QuestCanceler {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Identifier of the canceler.
     */
    private final String cancelerID;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Names to displaying in different languages.
     */
    private final Map<String, String> names;

    /**
     * Relevant data to cancel.
     */
    private final CancelData data;

    /**
     * Source package.
     */
    private final QuestPackage pack;

    /**
     * Custom item used for displaying.
     */
    @Nullable
    private final ItemID item;

    /**
     * Creates a new canceler.
     *
     * @param log           the custom logger for this class
     * @param cancelerID    the log identifier
     * @param pluginMessage the {@link PluginMessage} instance
     * @param names         the names used for displaying in different languages
     * @param item          the custom item used for displaying
     * @param pack          the {@link QuestPackage} of the canceler
     * @param cancelData    the relevant data to cancel a quest
     */
    public QuestCanceler(final BetonQuestLogger log, final String cancelerID, final PluginMessage pluginMessage,
                         final Map<String, String> names, @Nullable final ItemID item,
                         final QuestPackage pack, final CancelData cancelData) {
        this.log = log;
        this.cancelerID = cancelerID;
        this.names = names;
        this.item = item;
        this.data = cancelData;
        this.pack = pack;
        this.pluginMessage = pluginMessage;
    }

    /**
     * Checks conditions of this canceler to decide if it should be shown to the
     * player or not.
     *
     * @param profile the {@link Profile} of the player
     * @return true if all conditions are met, false otherwise
     */
    public boolean show(final Profile profile) {
        return data.conditions == null || BetonQuest.getInstance().getQuestTypeAPI().conditions(profile, data.conditions);
    }

    /**
     * Cancels the quest for specified player.
     * The conditions need to be checked with {@link #show(Profile)}.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    public void cancel(final OnlineProfile onlineProfile) {
        log.debug("Canceling the quest " + cancelerID + " for " + onlineProfile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(onlineProfile);
        // remove tags, points, objectives and journals
        removeSimple(data.tags, "tag", playerData::removeTag);
        removeSimple(data.points, "point", playerData::removePointsCategory);
        cancelObjectives(onlineProfile, playerData);
        if (data.journal != null) {
            final Journal journal = playerData.getJournal();
            removeSimple(data.journal, "journal entry", journal::removePointer);
            journal.update();
        }
        // teleport player to the location
        if (data.location != null) {
            try {
                log.debug("  Teleporting to new location");
                onlineProfile.getPlayer().teleport(data.location.getValue(onlineProfile));
            } catch (final QuestException e) {
                log.warn("Could not teleport to " + data.location, e);
            }
        }
        // fire all events
        if (data.events != null) {
            for (final EventID event : data.events) {
                BetonQuest.getInstance().getQuestTypeAPI().event(onlineProfile, event);
            }
        }
        // done
        log.debug("Quest removed!");
        final String questName = getName(onlineProfile);
        final String message = pluginMessage.getMessage(onlineProfile, "quest_canceled",
                new PluginMessage.Replacement("name", questName));
        try {
            Notify.get(pack, "quest_cancelled,quest_canceled,info").sendNotify(message, onlineProfile);
        } catch (final QuestException exception) {
            log.warn("The notify system was unable to play a sound for the 'quest_canceled' category in quest '"
                    + cancelerID + "'. Error was: '" + exception.getMessage() + "'");
        }
    }

    private void cancelObjectives(final OnlineProfile onlineProfile, final PlayerData playerData) {
        if (data.objectives != null) {
            for (final ObjectiveID objectiveID : data.objectives) {
                log.debug(objectiveID.getPackage(), "  Removing objective " + objectiveID);
                final Objective objective = BetonQuest.getInstance().getQuestTypeAPI().getObjective(objectiveID);
                if (objective == null) {
                    log.warn("Could not find objective " + objectiveID + " in QuestCanceler " + cancelerID);
                } else {
                    objective.cancelObjectiveForPlayer(onlineProfile);
                }
                playerData.removeRawObjective(objectiveID);
            }
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
        String questName = names.get(BetonQuest.getInstance().getPlayerDataStorage().get(profile).getLanguage());
        if (questName == null) {
            questName = names.get(Config.getLanguage());
        }
        if (questName == null) {
            questName = names.get("en");
        }
        if (questName == null) {
            log.warn("Quest name is not defined in canceler " + pack.getQuestPath() + "." + cancelerID);
            questName = "Quest";
        }
        return questName.replace("_", " ").replace("&", "§");
    }

    /**
     * Get the representing Item.
     *
     * @param profile the profile to build the item for
     * @return built item to visualize this canceler
     */
    public ItemStack getItem(final Profile profile) {
        ItemStack stack = new ItemStack(Material.BONE);
        if (item != null) {
            try {
                stack = new QuestItem(item).generate(1);
            } catch (final QuestException e) {
                log.warn("Could not load cancel button: " + e.getMessage(), e);
            }
        }
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName(profile));
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Relevant data for the cancel process.
     *
     * @param conditions the conditions which need to be fulfilled to use the canceler
     * @param location   the location to teleport the player to
     * @param events     the events to fire when the canceler is used
     * @param objectives the objectives to stop
     * @param tags       the tags  to remove
     * @param points     the points to remove
     * @param journal    the journal entries to remove
     */
    public record CancelData(@Nullable ConditionID[] conditions, @Nullable VariableLocation location,
                             @Nullable EventID[] events, @Nullable ObjectiveID[] objectives, @Nullable String[] tags,
                             @Nullable String[] points, @Nullable String[] journal) {
    }
}
