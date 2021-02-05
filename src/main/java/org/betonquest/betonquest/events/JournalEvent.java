package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Adds the entry to player's journal
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class JournalEvent extends QuestEvent {

    private final String name;
    private final boolean add;

    public JournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        final String first = instruction.next();
        if ("update".equalsIgnoreCase(first)) {
            name = null;
            add = false;
        } else {
            add = "add".equalsIgnoreCase(first);
            name = Utils.addPackage(instruction.getPackage(), instruction.next());
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
                BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_ENTRIES, new String[]{
                        name
                }));
            }
        } else {
            final PlayerData playerData = PlayerConverter.getPlayer(playerID) == null ? new PlayerData(playerID) : BetonQuest.getInstance().getPlayerData(playerID);
            final Journal journal = playerData.getJournal();
            if (add) {
                journal.addPointer(new Pointer(name, new Date().getTime()));
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "new_journal_entry", null, "new_journal_entry,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'new_journal_entry' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            } else if (name != null) {
                journal.removePointer(name);
            }
            journal.update();
        }
        return null;
    }

}
