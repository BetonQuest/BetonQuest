package org.betonquest.betonquest.quest.event.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The objective event, that adds, removes oder completes objectives.
 */
public class ObjectiveEvent implements NullableEvent {

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
     * All objectives affected by this event.
     */
    private final List<ObjectiveID> objectives;

    /**
     * The action to do with the objectives.
     */
    private final String action;

    /**
     * Creates a new ObjectiveEvent.
     *
     * @param betonQuest   the BetonQuest instance
     * @param log          the logger
     * @param questPackage the quest package of the instruction
     * @param objectives   the objectives to affect
     * @param action       the action to do with the objectives
     * @throws QuestException if the action is invalid
     */
    public ObjectiveEvent(final BetonQuest betonQuest, final BetonQuestLogger log, final QuestPackage questPackage, final List<ObjectiveID> objectives, final String action) throws QuestException {
        this.log = log;
        this.questPackage = questPackage;
        this.betonQuest = betonQuest;
        this.objectives = objectives;
        if (!Arrays.asList("start", "add", "delete", "remove", "complete", "finish").contains(action)) {
            throw new QuestException("Invalid action: " + action);
        }
        this.action = action.toLowerCase(Locale.ROOT);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ObjectiveID objectiveID : objectives) {
            final Objective objective = betonQuest.getObjective(objectiveID);
            if (objective == null) {
                throw new QuestException("Objective '" + objectiveID + "' is not defined, cannot run objective event");
            }
            if (profile == null) {
                handleStatic(objectiveID, objective);
            } else if (profile.getOnlineProfile().isEmpty()) {
                handleForOfflinePlayer(profile, objectiveID);
            } else {
                handleForOnlinePlayer(profile, objectiveID, objective);
            }
        }
    }

    private void handleStatic(final ObjectiveID objectiveID, final Objective objective) {
        if ("delete".equals(action) || "remove".equals(action)) {
            PlayerConverter.getOnlineProfiles().forEach(onlineProfile -> cancelObjectiveForOnlinePlayer(onlineProfile, objectiveID, objective));
            betonQuest.getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_OBJECTIVES, objectiveID.toString()));
        } else {
            log.warn(questPackage, "You tried to call an objective add / finish event in a static context! Only objective delete works here.");
        }
    }

    private void handleForOnlinePlayer(final Profile profile, final ObjectiveID objectiveID, final Objective objective) {
        switch (action.toLowerCase(Locale.ROOT)) {
            case "start", "add" -> BetonQuest.newObjective(profile, objectiveID);
            case "complete", "finish" -> objective.completeObjective(profile);
            default -> cancelObjectiveForOnlinePlayer(profile, objectiveID, objective);
        }
    }

    private void handleForOfflinePlayer(final Profile profile, final ObjectiveID objectiveID) {
        Bukkit.getScheduler().runTaskAsynchronously(betonQuest, () -> {
            final PlayerData playerData = new PlayerData(profile);
            switch (action.toLowerCase(Locale.ROOT)) {
                case "start", "add" -> playerData.addNewRawObjective(objectiveID);
                case "complete", "finish" ->
                        log.warn(questPackage, "Cannot complete objective for " + profile + ", because he is offline!");
                default -> playerData.removeRawObjective(objectiveID);
            }
        });
    }

    private void cancelObjectiveForOnlinePlayer(final Profile profile, final ObjectiveID objectiveID, final Objective objective) {
        objective.cancelObjectiveForPlayer(profile);
        betonQuest.getPlayerDataStorage().get(profile).removeRawObjective(objectiveID);
    }
}
