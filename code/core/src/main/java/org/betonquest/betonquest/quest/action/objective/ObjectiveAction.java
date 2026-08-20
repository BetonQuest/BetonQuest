package org.betonquest.betonquest.quest.action.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The objective action, that adds, removes oder completes objectives.
 */
public class ObjectiveAction implements NullableAction {

    /**
     * The BetonQuest instance.
     */
    private final Plugin plugin;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The profile provider.
     */
    private final ProfileProvider profileProvider;

    /**
     * The database saver.
     */
    private final Saver saver;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The player data storage.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The quest package.
     */
    private final QuestPackage questPackage;

    /**
     * All objectives affected by this action.
     */
    private final Argument<List<ObjectiveIdentifier>> objectives;

    /**
     * The action to do with the objectives.
     */
    private final String action;

    /**
     * Creates a new ObjectiveAction.
     *
     * @param plugin            the BetonQuest instance
     * @param log               the logger
     * @param profileProvider   the profile provider
     * @param saver             the database saver
     * @param objectiveManager  the objective manager
     * @param playerDataStorage the player data storage
     * @param questPackage      the quest package of the instruction
     * @param objectives        the objectives to affect
     * @param action            the action to do with the objectives
     * @throws QuestException if the action is invalid
     */
    public ObjectiveAction(final Plugin plugin, final BetonQuestLogger log, final ProfileProvider profileProvider, final Saver saver,
                           final ObjectiveManager objectiveManager, final PlayerDataStorage playerDataStorage,
                           final QuestPackage questPackage, final Argument<List<ObjectiveIdentifier>> objectives,
                           final String action) throws QuestException {
        this.plugin = plugin;
        this.profileProvider = profileProvider;
        this.saver = saver;
        this.log = log;
        this.objectiveManager = objectiveManager;
        this.playerDataStorage = playerDataStorage;
        this.questPackage = questPackage;
        this.objectives = objectives;
        if (!Arrays.asList("start", "add", "delete", "remove", "complete", "finish").contains(action)) {
            throw new QuestException("Invalid action: " + action);
        }
        this.action = action.toLowerCase(Locale.ROOT);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ObjectiveIdentifier objectiveID : objectives.getValue(profile)) {
            final Objective objective = objectiveManager.getObjective(objectiveID);
            if (profile == null) {
                handlePlayerless(objectiveID);
            } else if (profile.getOnlineProfile().isEmpty()) {
                handleForOfflinePlayer(profile, objectiveID);
            } else {
                handleForOnlinePlayer(profile, objectiveID, objective);
            }
        }
    }

    private void handlePlayerless(final ObjectiveIdentifier objectiveID) {
        if ("delete".equals(action) || "remove".equals(action)) {
            final ProfileProvider profileProvider = this.profileProvider;
            profileProvider.getOnlineProfiles().forEach(onlineProfile -> cancelObjectiveForOnlinePlayer(onlineProfile, objectiveID));
            this.saver.add(new Saver.Record(UpdateType.REMOVE_ALL_OBJECTIVES, objectiveID.toString()));
        } else {
            log.warn(questPackage, "You tried to call an objective add / finish action in an independent context! Only objective delete works here.");
        }
    }

    private void handleForOnlinePlayer(final Profile profile, final ObjectiveIdentifier objectiveID, final Objective objective) {
        switch (action.toLowerCase(Locale.ROOT)) {
            case "start", "add" -> objectiveManager.start(profile, objectiveID);
            case "complete", "finish" -> objective.getService().complete(profile);
            default -> cancelObjectiveForOnlinePlayer(profile, objectiveID);
        }
    }

    private void handleForOfflinePlayer(final Profile profile, final ObjectiveIdentifier objectiveID) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final PlayerData playerData = playerDataStorage.get(profile);
            switch (action.toLowerCase(Locale.ROOT)) {
                case "start", "add" -> playerData.addNewRawObjective(objectiveID);
                case "complete", "finish" ->
                        log.warn(questPackage, "Cannot complete objective for " + profile + ", because he is offline!");
                default -> playerData.removeRawObjective(objectiveID);
            }
        });
    }

    private void cancelObjectiveForOnlinePlayer(final Profile profile, final ObjectiveIdentifier objectiveID) {
        this.objectiveManager.cancel(profile, objectiveID);
        this.playerDataStorage.get(profile).removeRawObjective(objectiveID);
    }
}
