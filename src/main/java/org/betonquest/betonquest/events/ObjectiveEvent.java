package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (BetonQuest.getInstance().getObjective(objective) == null) {
            throw new QuestRuntimeException("Objective '" + objective + "' is not defined, cannot run objective event");
        }
        if (playerID == null) {
            if ("delete".equals(action) || "remove".equals(action)) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                    playerData.removeRawObjective(objective);
                }
                BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_OBJECTIVES, objective.toString()));
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
            }.runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            switch (action.toLowerCase(Locale.ROOT)) {
                case "start":
                case "add":
                    BetonQuest.newObjective(playerID, objective);
                    break;
                case "delete":
                case "remove":
                    BetonQuest.getInstance().getObjective(objective).removePlayer(playerID);
                    BetonQuest.getInstance().getPlayerData(playerID).removeRawObjective(objective);
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
}
