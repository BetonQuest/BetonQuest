package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Starts an objective for the player
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
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

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition"})
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final BetonQuest betonquest = BetonQuest.getInstance();
        if (betonquest.getObjective(objective) == null) {
            throw new QuestRuntimeException("Objective '" + objective + "' is not defined, cannot run objective event");
        }
        if (playerID == null) {
            if ("delete".equals(action) || "remove".equals(action)) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    final String uuid = PlayerConverter.getID(player);
                    cancelObjectiveForOnlinePlayer(uuid, betonquest);
                });
                betonquest.getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_OBJECTIVES, objective.toString()));
            } else {
                LogUtils.getLogger().log(Level.WARNING, "You tried to call an objective add / finish event in a static context! Only objective delete works here.");
            }
        } else if (PlayerConverter.getPlayer(playerID) == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final PlayerData playerData = new PlayerData(playerID);
                    switch (action.toLowerCase(Locale.ROOT)) {
                        case "start":
                        case "add":
                            playerData.addNewRawObjective(objective);
                            break;
                        case "delete":
                        case "remove":
                            playerData.removeRawObjective(objective);
                            break;
                        case "complete":
                        case "finish":
                            LogUtils.getLogger().log(Level.WARNING, "Cannot complete objective for offline player!");
                            break;
                        default:
                            break;
                    }
                }
            }.runTaskAsynchronously(betonquest);
        } else {
            switch (action.toLowerCase(Locale.ROOT)) {
                case "start":
                case "add":
                    BetonQuest.newObjective(playerID, objective);
                    break;
                case "delete":
                case "remove":
                    cancelObjectiveForOnlinePlayer(playerID, betonquest);
                    break;
                case "complete":
                case "finish":
                    BetonQuest.getInstance().getObjective(objective).completeObjective(playerID);
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    private void cancelObjectiveForOnlinePlayer(final String playerID, final BetonQuest betonquest) {
        betonquest.getObjective(objective).removePlayer(playerID);
        betonquest.getPlayerData(playerID).removeRawObjective(objective);
    }
}
