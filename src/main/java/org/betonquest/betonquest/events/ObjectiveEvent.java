package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Starts an objective for the player.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class ObjectiveEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(ObjectiveEvent.class);

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
        staticness = true;
        betonQuest = BetonQuest.getInstance();

        action = instruction.next().toLowerCase(Locale.ROOT);
        if (!Arrays.asList(new String[]{"start", "add", "delete", "remove", "complete", "finish"}).contains(action)) {
            throw new InstructionParseException("Unknown action: " + action);
        }
        objectives = instruction.getList(instruction::getObjective);
        persistent = !"complete".equalsIgnoreCase(action);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        for (final ObjectiveID objective : objectives) {
            if (betonQuest.getObjective(objective) == null) {
                throw new QuestRuntimeException("Objective '" + objective + "' is not defined, cannot run objective event");
            }
            if (profile == null) {
                if ("delete".equals(action) || "remove".equals(action)) {
                    PlayerConverter.getOnlineProfiles().forEach(onlineProfile -> cancelObjectiveForOnlinePlayer(onlineProfile, objective));
                    betonQuest.getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_OBJECTIVES, objective.toString()));
                } else {
                    LOG.warn(instruction.getPackage(), "You tried to call an objective add / finish event in a static context! Only objective delete works here.");
                }
            } else if (profile.getOnlineProfile().isEmpty()) {
                Bukkit.getScheduler().runTaskAsynchronously(betonQuest, () -> {
                    final PlayerData playerData = new PlayerData(profile);
                    switch (action.toLowerCase(Locale.ROOT)) {
                        case "start", "add" -> playerData.addNewRawObjective(objective);
                        case "delete", "remove" -> playerData.removeRawObjective(objective);
                        case "complete", "finish" ->
                                LOG.warn(instruction.getPackage(), "Cannot complete objective for " + profile + ", because he is offline!");
                        default -> {
                        }
                    }
                });
            } else {
                switch (action.toLowerCase(Locale.ROOT)) {
                    case "start", "add" -> BetonQuest.newObjective(profile, objective);
                    case "delete", "remove" -> cancelObjectiveForOnlinePlayer(profile, objective);
                    case "complete", "finish" -> betonQuest.getObjective(objective).completeObjective(profile);
                    default -> {
                    }
                }
            }
        }
        return null;
    }

    private void cancelObjectiveForOnlinePlayer(final Profile profile, final ObjectiveID objective) {
        betonQuest.getObjective(objective).cancelObjectiveForPlayer(profile);
        betonQuest.getPlayerData(profile).removeRawObjective(objective);
    }
}
