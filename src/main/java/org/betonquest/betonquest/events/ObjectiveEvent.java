package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Locale;

/**
 * Starts an objective for the player.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
@CustomLog
public class ObjectiveEvent extends QuestEvent {

    private final ObjectiveID objective;
    private final String action;

    public ObjectiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        action = instruction.next().toLowerCase(Locale.ROOT);
        objective = instruction.getObjective();
        if (!Arrays.asList(new String[]{"start", "add", "delete", "remove", "complete", "finish"})
                .contains(action)) {
            throw new InstructionParseException("Unknown action: " + action);
        }
        persistent = !"complete".equalsIgnoreCase(action);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final BetonQuest betonquest = BetonQuest.getInstance();
        if (betonquest.getObjective(objective) == null) {
            throw new QuestRuntimeException("Objective '" + objective + "' is not defined, cannot run objective event");
        }
        if (profile == null) {
            if ("delete".equals(action) || "remove".equals(action)) {
                PlayerConverter.getOnlineProfiles().forEach(onlineProfile -> cancelObjectiveForOnlinePlayer(onlineProfile, betonquest));
                betonquest.getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_OBJECTIVES, objective.toString()));
            } else {
                LOG.warn(instruction.getPackage(), "You tried to call an objective add / finish event in a static context! Only objective delete works here.");
            }
        } else if (profile.getPlayer() == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final PlayerData playerData = new PlayerData(profile);
                    switch (action.toLowerCase(Locale.ROOT)) {
                        case "start", "add" -> playerData.addNewRawObjective(objective);
                        case "delete", "remove" -> playerData.removeRawObjective(objective);
                        case "complete", "finish" ->
                                LOG.warn(instruction.getPackage(), "Cannot complete objective for offline player!");
                        default -> {
                        }
                    }
                }
            }.runTaskAsynchronously(betonquest);
        } else {
            switch (action.toLowerCase(Locale.ROOT)) {
                case "start", "add" -> BetonQuest.newObjective(profile, objective);
                case "delete", "remove" -> cancelObjectiveForOnlinePlayer(profile, betonquest);
                case "complete", "finish" -> betonquest.getObjective(objective).completeObjective(profile);
                default -> {
                }
            }
        }
        return null;
    }

    private void cancelObjectiveForOnlinePlayer(final Profile profile, final BetonQuest betonquest) {
        betonquest.getObjective(objective).cancelObjectiveForPlayer(profile);
        betonquest.getPlayerData(profile).removeRawObjective(objective);
    }
}
