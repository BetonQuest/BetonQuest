package org.betonquest.betonquest.events.action.journal;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.events.JournalEvent;
import org.betonquest.betonquest.events.action.DoNothingEventBulkAction;
import org.betonquest.betonquest.events.action.EventAction;
import org.betonquest.betonquest.events.action.EventBulkAction;
import org.betonquest.betonquest.events.action.InfoNotificationSender;
import org.betonquest.betonquest.events.action.NoNotificationSender;
import org.betonquest.betonquest.events.action.NotificationSender;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.betonquest.betonquest.utils.Utils;

import java.util.Locale;

public class JournalEventFactory implements QuestEventFactory {
    @Override
    public JournalEvent parseEventInstruction(final Instruction instruction) throws InstructionParseException {
        final JournalChanger journalChanger;
        final NotificationSender notificationSender;
        final EventAction journalAction;
        final EventBulkAction eventBulkAction;

        final String action = instruction.next();
        switch (action.toLowerCase(Locale.ROOT)) {
            case "update" -> {
                journalChanger = new NoActionJournalChanger();
                notificationSender = new NoNotificationSender();
                journalAction = new JournalEventAction(journalChanger, notificationSender);
                eventBulkAction = new DoNothingEventBulkAction();
            }
            case "add" -> {
                final String entryName = Utils.addPackage(instruction.getPackage(), instruction.next());
                journalChanger = new AddEntryJournalChanger(entryName);
                notificationSender = new InfoNotificationSender("new_journal_entry", instruction.getPackage(), instruction.getID().getFullID());
                journalAction = new JournalEventAction(journalChanger, notificationSender);
                eventBulkAction = new DoNothingEventBulkAction();
            }
            case "delete" -> {
                final String entryName = Utils.addPackage(instruction.getPackage(), instruction.next());
                journalChanger = new RemoveEntryJournalChanger(entryName);
                notificationSender = new NoNotificationSender();
                journalAction = new JournalEventAction(journalChanger, notificationSender);
                eventBulkAction = new RemoveJournalEntryEventBulkAction(entryName, journalAction);
            }
            default -> throw new InstructionParseException("Unknown journal action: " + action);
        }
        return new JournalEvent(instruction, journalAction, eventBulkAction);
    }
}
