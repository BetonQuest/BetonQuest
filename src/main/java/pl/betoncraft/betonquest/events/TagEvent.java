package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Adds or removes tags from the player
 */
@SuppressWarnings("PMD.CommentRequired")
public class TagEvent extends QuestEvent {

    protected final String[] tags;
    protected final boolean add;

    public TagEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        staticness = true;
        add = "add".equalsIgnoreCase(instruction.next());
        tags = instruction.getArray();
        for (int i = 0; i < tags.length; i++) {
            tags[i] = Utils.addPackage(instruction.getPackage(), tags[i]);
        }
    }

    @Override
    protected Void execute(final String playerID) {
        if (playerID == null) {
            if (!add) {
                for (final String tag : tags) {
                    for (final Player p : Bukkit.getOnlinePlayers()) {
                        final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                        playerData.removeTag(tag);
                    }
                    BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_TAGS, tag));
                }
            }
        } else if (PlayerConverter.getPlayer(playerID) == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final PlayerData playerData = new PlayerData(playerID);
                    if (add) {
                        for (final String tag : tags) {
                            playerData.addTag(tag);
                        }
                    } else {
                        for (final String tag : tags) {
                            playerData.removeTag(tag);
                        }
                    }
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            if (add) {
                for (final String tag : tags) {
                    playerData.addTag(tag);
                }
            } else {
                for (final String tag : tags) {
                    playerData.removeTag(tag);
                }
            }
        }
        return null;
    }
}
