package org.betonquest.betonquest.feature;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a quest canceler, which cancels quests for players.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class QuestCanceler {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Player Data storage.
     */
    private final PlayerDataStorage playerStorage;

    /**
     * Identifier of the canceler.
     */
    private final String cancelerID;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Names to displaying in different languages.
     */
    private final Text names;

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
     * The notification for canceling the quest.
     */
    private final IngameNotificationSender notificationSender;

    /**
     * Creates a new canceler.
     *
     * @param log           the custom logger for this class
     * @param questTypeApi  the Quest Type API
     * @param playerStorage the player data storage
     * @param cancelerID    the log identifier
     * @param featureApi    the Feature API
     * @param pluginMessage the {@link PluginMessage} instance
     * @param names         the names used for displaying in different languages
     * @param item          the custom item used for displaying
     * @param pack          the {@link QuestPackage} of the canceler
     * @param cancelData    the relevant data to cancel a quest
     */
    public QuestCanceler(final BetonQuestLogger log, final QuestTypeApi questTypeApi, final PlayerDataStorage playerStorage,
                         final String cancelerID, final FeatureApi featureApi, final PluginMessage pluginMessage,
                         final Text names, @Nullable final ItemID item, final QuestPackage pack, final CancelData cancelData) {
        this.log = log;
        this.questTypeApi = questTypeApi;
        this.playerStorage = playerStorage;
        this.cancelerID = cancelerID;
        this.featureApi = featureApi;
        this.pluginMessage = pluginMessage;
        this.names = names;
        this.item = item;
        this.data = cancelData;
        this.pack = pack;
        this.notificationSender = new IngameNotificationSender(log, pluginMessage, pack, cancelerID, NotificationLevel.INFO, "quest_canceled");
    }

    /**
     * Checks conditions of this canceler to decide if it is cancelable.
     *
     * @param profile the {@link Profile} of the player
     * @return true if all conditions are met, false otherwise
     * @throws QuestException if the conditions cannot be checked
     */
    public boolean isCancelable(final Profile profile) throws QuestException {
        return questTypeApi.conditions(profile, data.conditions.getValue(profile));
    }

    /**
     * Cancels the quest for specified player.
     * The conditions need to be checked with {@link #isCancelable(Profile)}.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param bypass        whether the defined conditions should be ignored
     */
    public void cancel(final OnlineProfile onlineProfile, final boolean bypass) {
        try {
            if (!bypass && !isCancelable(onlineProfile)) {
                log.debug(pack, "Attempted to cancel the quest " + cancelerID + " for " + onlineProfile
                        + ", but the conditions are note met");
                return;
            }
        } catch (final QuestException e) {
            log.warn(pack, "Could not check conditions for canceling the quest " + cancelerID + " for " + onlineProfile, e);
            return;
        }
        log.debug(pack, "Canceling the quest " + cancelerID + " for " + onlineProfile);
        final PlayerData playerData = playerStorage.get(onlineProfile);
        removeSimple(onlineProfile, data.tags, "tag", playerData::removeTag);
        removeSimple(onlineProfile, data.points, "point", playerData::removePointsCategory);
        cancelObjectives(onlineProfile, playerData);
        removeEntries(onlineProfile, playerData);
        executeEvents(onlineProfile);
        teleport(onlineProfile);
        log.debug("Quest removed!");
        final Component questName = getName(onlineProfile);
        notificationSender.sendNotification(onlineProfile, new VariableReplacement("name", questName));
    }

    private void removeSimple(final Profile profile, final Variable<List<String>> toRemove, final String logIdentifier,
                              final Consumer<String> action) {
        try {
            for (final String entry : toRemove.getValue(profile)) {
                log.debug(pack, "  Removing " + logIdentifier + " " + entry);
                if (entry.contains(".")) {
                    action.accept(entry);
                } else {
                    action.accept(pack.getQuestPath() + "." + entry);
                }
            }
        } catch (final QuestException e) {
            log.warn(pack, "Cannot remove " + logIdentifier + " in QuestCanceler " + cancelerID + ": " + e.getMessage(), e);
        }
    }

    private void cancelObjectives(final Profile profile, final PlayerData playerData) {
        try {
            for (final ObjectiveID objectiveID : data.objectives.getValue(profile)) {
                log.debug(objectiveID.getPackage(), "  Removing objective " + objectiveID);
                final Objective objective = questTypeApi.getObjective(objectiveID);
                objective.cancelObjectiveForPlayer(profile);
                playerData.removeRawObjective(objectiveID);
            }
        } catch (final QuestException e) {
            log.warn(pack, "Cannot cancel objectives in QuestCanceler " + cancelerID + ": " + e.getMessage(), e);
        }
    }

    private void removeEntries(final Profile profile, final PlayerData playerData) {
        try {
            final Journal journal = playerData.getJournal(pluginMessage);
            for (final JournalEntryID entry : data.journal.getValue(profile)) {
                log.debug(pack, "  Removing journal entry " + entry);
                journal.removePointer(entry);
            }
            journal.update();
        } catch (final QuestException e) {
            log.warn(pack, "Cannot remove journal entries in QuestCanceler " + cancelerID + ": " + e.getMessage(), e);
        }
    }

    private void executeEvents(final OnlineProfile onlineProfile) {
        try {
            for (final EventID event : data.events.getValue(onlineProfile)) {
                questTypeApi.event(onlineProfile, event);
            }
        } catch (final QuestException e) {
            log.warn(pack, "Cannot execute events in QuestCanceler " + cancelerID + ": " + e.getMessage(), e);
        }
    }

    private void teleport(final OnlineProfile onlineProfile) {
        if (data.location == null) {
            return;
        }
        try {
            log.debug(pack, "  Teleporting to new location");
            onlineProfile.getPlayer().teleport(data.location.getValue(onlineProfile));
        } catch (final QuestException e) {
            log.warn(pack, "Could not teleport to " + data.location, e);
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
    public Component getName(final Profile profile) {
        try {
            return names.asComponent(profile);
        } catch (final QuestException e) {
            log.warn(pack, "Could not resolve Quest name in canceler '" + pack.getQuestPath() + "." + cancelerID + "': "
                    + e.getMessage(), e);
            return Component.text("Quest");
        }
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
                stack = featureApi.getItem(item, profile).generate(1);
            } catch (final QuestException e) {
                log.warn(pack, "Could not load cancel button: " + e.getMessage(), e);
            }
        }
        stack.editMeta(meta -> meta.displayName(getName(profile)));
        return stack;
    }

    /**
     * Relevant data for the cancel process.
     *
     * @param conditions the conditions which need to be fulfilled to use the canceler
     * @param events     the events to fire when the canceler is used
     * @param objectives the objectives to stop
     * @param tags       the tags  to remove
     * @param points     the points to remove
     * @param journal    the journal entries to remove
     * @param location   the location to teleport the player to
     */
    public record CancelData(Variable<List<ConditionID>> conditions, Variable<List<EventID>> events,
                             Variable<List<ObjectiveID>> objectives, Variable<List<String>> tags,
                             Variable<List<String>> points, Variable<List<JournalEntryID>> journal,
                             @Nullable Variable<Location> location) {
    }
}
