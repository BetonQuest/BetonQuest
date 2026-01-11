package org.betonquest.betonquest.quest.action.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The objective action, that adds, removes oder completes objectives.
 */
public class ObjectiveAction implements NullableAction {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The quest package.
     */
    private final QuestPackage questPackage;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * All objectives affected by this action.
     */
    private final Argument<List<ObjectiveIdentifier>> objectives;

    /**
     * API for starting objectives.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Factory to create new Player Data.
     */
    private final PlayerDataFactory playerDataFactory;

    /**
     * The action to do with the objectives.
     */
    private final String action;

    /**
     * Creates a new ObjectiveAction.
     *
     * @param betonQuest        the BetonQuest instance
     * @param questTypeApi      the class for starting objectives
     * @param log               the logger
     * @param questPackage      the quest package of the instruction
     * @param objectives        the objectives to affect
     * @param playerDataFactory the factory to create player data
     * @param action            the action to do with the objectives
     * @throws QuestException if the action is invalid
     */
    public ObjectiveAction(final BetonQuest betonQuest, final BetonQuestLogger log, final QuestTypeApi questTypeApi,
                           final QuestPackage questPackage, final Argument<List<ObjectiveIdentifier>> objectives, final PlayerDataFactory playerDataFactory, final String action) throws QuestException {
        this.log = log;
        this.questPackage = questPackage;
        this.betonQuest = betonQuest;
        this.objectives = objectives;
        this.questTypeApi = questTypeApi;
        this.playerDataFactory = playerDataFactory;
        if (!Arrays.asList("start", "add", "delete", "remove", "complete", "finish").contains(action)) {
            throw new QuestException("Invalid action: " + action);
        }
        this.action = action.toLowerCase(Locale.ROOT);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ObjectiveIdentifier objectiveID : objectives.getValue(profile)) {
            final Objective objective = questTypeApi.getObjective(objectiveID);
            if (profile == null) {
                handleStatic(objectiveID);
            } else if (profile.getOnlineProfile().isEmpty()) {
                handleForOfflinePlayer(profile, objectiveID);
            } else {
                handleForOnlinePlayer(profile, objectiveID, objective);
            }
        }
    }

    private void handleStatic(final ObjectiveIdentifier objectiveID) {
        if ("delete".equals(action) || "remove".equals(action)) {
            final ProfileProvider profileProvider = betonQuest.getProfileProvider();
            profileProvider.getOnlineProfiles().forEach(onlineProfile -> cancelObjectiveForOnlinePlayer(onlineProfile, objectiveID));
            betonQuest.getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_OBJECTIVES, objectiveID.toString()));
        } else {
            log.warn(questPackage, "You tried to call an objective add / finish action in a static context! Only objective delete works here.");
        }
    }

    private void handleForOnlinePlayer(final Profile profile, final ObjectiveIdentifier objectiveID, final Objective objective) {
        switch (action.toLowerCase(Locale.ROOT)) {
            case "start", "add" -> questTypeApi.newObjective(profile, objectiveID);
            case "complete", "finish" -> objective.getService().complete(profile);
            default -> cancelObjectiveForOnlinePlayer(profile, objectiveID);
        }
    }

    private void handleForOfflinePlayer(final Profile profile, final ObjectiveIdentifier objectiveID) {
        Bukkit.getScheduler().runTaskAsynchronously(betonQuest, () -> {
            final PlayerData playerData = playerDataFactory.createPlayerData(profile);
            switch (action.toLowerCase(Locale.ROOT)) {
                case "start", "add" -> playerData.addNewRawObjective(objectiveID);
                case "complete", "finish" ->
                        log.warn(questPackage, "Cannot complete objective for " + profile + ", because he is offline!");
                default -> playerData.removeRawObjective(objectiveID);
            }
        });
    }

    private void cancelObjectiveForOnlinePlayer(final Profile profile, final ObjectiveIdentifier objectiveID) {
        betonQuest.getQuestTypeApi().cancelObjective(profile, objectiveID);
        betonQuest.getPlayerDataStorage().get(profile).removeRawObjective(objectiveID);
    }
}
