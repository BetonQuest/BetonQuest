package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Starts an objective for the player.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ObjectiveEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
     * Parses the users' instruction for use when the event is executed.
     *
     * @param instruction the instruction to parse
     * @throws InstructionParseException if the instruction is invalid
     */
    public ObjectiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        staticness = true;
        betonQuest = BetonQuest.getInstance();

        action = instruction.next().toLowerCase(Locale.ROOT);
        if (!Arrays.asList("start", "add", "delete", "remove", "complete", "finish").contains(action)) {
            throw new InstructionParseException("Unknown action: " + action);
        }
        objectives = instruction.getList(instruction::getObjective);
        persistent = !"complete".equalsIgnoreCase(action);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        for (final ObjectiveID objectiveID : objectives) {
            final Objective objective = betonQuest.getObjective(objectiveID);
            if (objective == null) {
                throw new QuestRuntimeException("Objective '" + objectiveID + "' is not defined, cannot run objective event");
            }
            if (profile == null) {
                if ("delete".equals(action) || "remove".equals(action)) {
                    PlayerConverter.getOnlineProfiles().forEach(onlineProfile -> cancelObjectiveForOnlinePlayer(onlineProfile, objectiveID, objective));
                    betonQuest.getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_OBJECTIVES, objectiveID.toString()));
                } else {
                    log.warn(instruction.getPackage(), "You tried to call an objective add / finish event in a static context! Only objective delete works here.");
                }
            } else if (profile.getOnlineProfile().isEmpty()) {
                Bukkit.getScheduler().runTaskAsynchronously(betonQuest, () -> {
                    final PlayerData playerData = new PlayerData(profile);
                    switch (action.toLowerCase(Locale.ROOT)) {
                        case "start", "add" -> playerData.addNewRawObjective(objectiveID);
                        case "delete", "remove" -> playerData.removeRawObjective(objectiveID);
                        case "complete", "finish" ->
                                log.warn(instruction.getPackage(), "Cannot complete objective for " + profile + ", because he is offline!");
                        default -> {
                        }
                    }
                });
            } else {
                switch (action.toLowerCase(Locale.ROOT)) {
                    case "start", "add" -> BetonQuest.newObjective(profile, objectiveID);
                    case "delete", "remove" -> cancelObjectiveForOnlinePlayer(profile, objectiveID, objective);
                    case "complete", "finish" -> objective.completeObjective(profile);
                    default -> {
                    }
                }
            }
        }
        return null;
    }

    private void cancelObjectiveForOnlinePlayer(final Profile profile, final ObjectiveID objectiveID, final Objective objective) {
        objective.cancelObjectiveForPlayer(profile);
        betonQuest.getPlayerData(profile).removeRawObjective(objectiveID);
    }
}
