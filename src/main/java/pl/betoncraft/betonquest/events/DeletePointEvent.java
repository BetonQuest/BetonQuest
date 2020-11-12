package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Modifies player's points
 */
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
