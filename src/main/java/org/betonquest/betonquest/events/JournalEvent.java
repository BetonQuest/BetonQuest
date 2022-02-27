package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.events.action.InfoNotificationSender;
import org.betonquest.betonquest.events.action.NoNotificationSender;
import org.betonquest.betonquest.events.action.NotificationSender;
import org.betonquest.betonquest.events.action.journal.AddEntryJournalChanger;
import org.betonquest.betonquest.events.action.journal.JournalChanger;
import org.betonquest.betonquest.events.action.journal.NoActionJournalChanger;
import org.betonquest.betonquest.events.action.journal.RemoveEntryJournalChanger;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Adds the entry to player's journal.
 */
@CustomLog
public class JournalEvent extends QuestEvent {

    /**
     * If the event deletes a journal entry.
     */
    private final boolean delete;

    /**
     * Name of the journal entry to change.
     */
    private final String entryName;

    /**
     * Change to apply to a journal when the event is executed.
     */
    private final JournalChanger journalChanger;

    /**
     * Notification to send after the journal was changed.
     */
    private final NotificationSender notificationSender;

    /**
     * Create JournalEvent from Instruction.
     *
     * @param instruction instruction to parse.
     * @throws InstructionParseException if the instruction contains errors
     */
    public JournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        final String action = instruction.next();
        switch (action.toLowerCase(Locale.ROOT)) {
            case "update":
                delete = false;
                entryName = null;
                journalChanger = new NoActionJournalChanger();
                notificationSender = new NoNotificationSender();
                break;
            case "add":
                delete = false;
                entryName = Utils.addPackage(instruction.getPackage(), instruction.next());
                journalChanger = new AddEntryJournalChanger(entryName);
                notificationSender = new InfoNotificationSender("new_journal_entry", instruction.getPackage(), getFullId());
                break;
            case "delete":
                delete = true;
                entryName = Utils.addPackage(instruction.getPackage(), instruction.next());
                journalChanger = new RemoveEntryJournalChanger(entryName);
                notificationSender = new NoNotificationSender();
                break;
            default:
                throw new InstructionParseException("Unknown journal action: " + action);
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        if (playerID != null) {
            final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(playerID);
            final Journal journal = playerData.getJournal();
            journalChanger.changeJournal(journal);
            journal.update();
            notificationSender.sendNotification(playerID);
        } else if (delete) {
            for (final Player p : Bukkit.getOnlinePlayers()) {
                final PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                final Journal journal = playerData.getJournal();
                journal.removePointer(entryName);
                journal.update();
            }
            BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_ENTRIES, entryName));
        }
        return null;
    }

}
