package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.event.DatabaseSaverStaticEvent;
import org.betonquest.betonquest.quest.event.DoNothingStaticEvent;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.SequentialStaticEvent;
import org.betonquest.betonquest.util.Utils;

import java.time.InstantSource;
import java.util.Locale;

/**
 * Factory to create journal events from {@link Instruction}s.
 */
public class JournalEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * BetonQuest instance to provide to events.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The instant source to provide to events.
     */
    private final InstantSource instantSource;

    /**
     * The saver to inject into database-using events.
     */
    private final Saver saver;

    /**
     * The current active profile provider instance
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the journal event factory.
     *
     * @param loggerFactory logger factory to use
     * @param pluginMessage the {@link PluginMessage} instance
     * @param dataStorage   storage for used player data
     * @param instantSource instant source to pass on
     * @param saver         database saver to use
     */
    public JournalEventFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage, final PlayerDataStorage dataStorage, final InstantSource instantSource, final Saver saver, final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.dataStorage = dataStorage;
        this.instantSource = instantSource;
        this.saver = saver;
        this.profileProvider = profileProvider;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "update" -> createJournalUpdateEvent();
            case "add" -> createJournalAddEvent(instruction);
            case "delete" -> createJournalDeleteEvent(instruction);
            default -> throw new QuestException("Unknown journal action: " + action);
        };
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "update", "add" -> new DoNothingStaticEvent();
            case "delete" -> createStaticJournalDeleteEvent(instruction);
            default -> throw new QuestException("Unknown journal action: " + action);
        };
    }

    private JournalEvent createJournalDeleteEvent(final Instruction instruction) throws QuestException {
        final String entryName = Utils.addPackage(instruction.getPackage(), instruction.getPart(2));
        final JournalChanger journalChanger = new RemoveEntryJournalChanger(entryName);
        final NotificationSender notificationSender = new NoNotificationSender();
        return new JournalEvent(dataStorage, journalChanger, notificationSender);
    }

    private JournalEvent createJournalAddEvent(final Instruction instruction) throws QuestException {
        final String entryName = Utils.addPackage(instruction.getPackage(), instruction.getPart(2));
        final JournalChanger journalChanger = new AddEntryJournalChanger(instantSource, entryName);
        final NotificationSender notificationSender = new IngameNotificationSender(loggerFactory.create(JournalEvent.class),
                pluginMessage, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, "new_journal_entry");
        return new JournalEvent(dataStorage, journalChanger, notificationSender);
    }

    private JournalEvent createJournalUpdateEvent() {
        final JournalChanger journalChanger = new NoActionJournalChanger();
        final NotificationSender notificationSender = new NoNotificationSender();
        return new JournalEvent(dataStorage, journalChanger, notificationSender);
    }

    private StaticEvent createStaticJournalDeleteEvent(final Instruction instruction) throws QuestException {
        final JournalEvent journalDeleteEvent = createJournalDeleteEvent(instruction.copy());
        final String entryName = Utils.addPackage(instruction.getPackage(), instruction.getPart(2));
        return new SequentialStaticEvent(
                new OnlineProfileGroupStaticEventAdapter(profileProvider::getOnlineProfiles, journalDeleteEvent),
                new DatabaseSaverStaticEvent(saver, () -> new Saver.Record(UpdateType.REMOVE_ALL_ENTRIES, entryName)));
    }
}
