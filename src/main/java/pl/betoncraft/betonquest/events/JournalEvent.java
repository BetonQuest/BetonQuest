package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.Pointer;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Date;

/**
 * Adds the entry to player's journal
 */
public class JournalEvent extends QuestEvent {

    private final String name;
    private final boolean add;

    public JournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        final String first = instruction.next();
        if (first.equalsIgnoreCase("update")) {
            name = null;
            add = false;
        } else {
            add = first.equalsIgnoreCase("add");
            name = Utils.addPackage(instruction.getPackage(), instruction.next());
        }
    }

    @Override
    protected Void execute(final String playerID) {
        if (playerID == null) {
            if (!add && name != null) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                    final Journal journal = playerData.getJournal();
                    journal.removePointer(name);
                    journal.update();
                }
                BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_ENTRIES, new String[]{
                        name
                }));
            }
        }
        else {
            final PlayerData playerData = PlayerConverter.getPlayer(playerID) == null ? new PlayerData(playerID) : BetonQuest.getInstance().getPlayerData(playerID);
            final Journal journal = playerData.getJournal();
            if (add) {
                journal.addPointer(new Pointer(name, new Date().getTime()));
                Config.sendNotify(playerID, "new_journal_entry", null, "new_journal_entry,info");
            } else if (name != null) {
                journal.removePointer(name);
            }
            journal.update();
        }
        return null;
    }

}
