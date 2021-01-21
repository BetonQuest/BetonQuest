package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Modifies player's points
 */
@SuppressWarnings("PMD.CommentRequired")
public class DeletePointEvent extends QuestEvent {

    protected final String category;

    public DeletePointEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        staticness = true;
        category = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        if (playerID == null) {
            for (final Player p : Bukkit.getOnlinePlayers()) {
                final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                playerData.removePointsCategory(category);
            }
            BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_POINTS, new String[]{
                    category
            }));
        } else if (PlayerConverter.getPlayer(playerID) == null) {
            final PlayerData playerData = new PlayerData(playerID);
            playerData.removePointsCategory(category);
        } else {
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            playerData.removePointsCategory(category);
        }
        return null;
    }
}
