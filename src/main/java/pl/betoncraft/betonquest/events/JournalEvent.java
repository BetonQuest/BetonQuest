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
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Adds the entry to player's journal.
 */
@SuppressWarnings("PMD.CommentRequired")
public class JournalEvent extends QuestEvent {

    private final String name;
    private final boolean add;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public JournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        final String action = instruction.next();
        switch (action.toLowerCase(Locale.ROOT)) {
            case "update":
            case "refresh":
            case "reload":
                name = null;
                add = false;
                break;
            case "add":
                name = Utils.addPackage(instruction.getPackage(), instruction.next());
                add = true;
                break;
            case "delete":
            case "remove":
            case "del":
            case "rm":
                name = Utils.addPackage(instruction.getPackage(), instruction.next());
                add = false;
                break;
            default:
                throw new InstructionParseException("Unknown journal action: " + action);
        }
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        if (playerID == null) {
            if (!add && name != null) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                    final Journal journal = playerData.getJournal();
                    journal.removePointer(name);
                    journal.update();
                }
                BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_ENTRIES, name));
            }
        } else {
            final PlayerData playerData = PlayerConverter.getPlayer(playerID) == null ? new PlayerData(playerID) : BetonQuest.getInstance().getPlayerData(playerID);
            final Journal journal = playerData.getJournal();
            if (add) {
                journal.addPointer(new Pointer(name, new Date().getTime()));
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "new_journal_entry", null, "new_journal_entry,info");
                } catch (final QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'new_journal_entry' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowable(exception);
                }
            } else if (name != null) {
                journal.removePointer(name);
            }
            journal.update();
        }
        return null;
    }

}
