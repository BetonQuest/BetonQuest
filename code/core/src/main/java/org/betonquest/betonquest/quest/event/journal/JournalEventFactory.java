package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessEvent;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;

import java.time.InstantSource;
import java.util.Locale;

/**
 * Factory to create journal events from {@link Instruction}s.
 */
public class JournalEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Logger factory to create a logger for the events.
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
     * The current active profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the journal event factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param pluginMessage   the {@link PluginMessage} instance
     * @param dataStorage     storage for used player data
     * @param instantSource   instant source to pass on
     * @param saver           database saver to use
     * @param profileProvider the profile provider
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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "update" -> createJournalUpdateEvent();
            case "add" -> createJournalAddEvent(instruction);
            case "delete" -> createJournalDeleteEvent(instruction);
            default -> throw new QuestException("Unknown journal action: " + action);
        };
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "update", "add" -> new DoNothingPlayerlessEvent();
            case "delete" -> createStaticJournalDeleteEvent(instruction);
            default -> throw new QuestException("Unknown journal action: " + action);
        };
    }

    private JournalEvent createJournalDeleteEvent(final Instruction instruction) throws QuestException {
        final Variable<JournalEntryID> entryID = instruction.get(instruction.getPart(2), JournalEntryID::new);
        final JournalChanger journalChanger = new RemoveEntryJournalChanger(entryID);
        final NotificationSender notificationSender = new NoNotificationSender();
        return new JournalEvent(dataStorage, journalChanger, notificationSender);
    }

    private JournalEvent createJournalAddEvent(final Instruction instruction) throws QuestException {
        final Variable<JournalEntryID> entryID = instruction.get(instruction.getPart(2), JournalEntryID::new);
        final JournalChanger journalChanger = new AddEntryJournalChanger(instantSource, entryID);
        final NotificationSender notificationSender = new IngameNotificationSender(loggerFactory.create(JournalEvent.class),
                pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.INFO, "new_journal_entry");
        return new JournalEvent(dataStorage, journalChanger, notificationSender);
    }

    private JournalEvent createJournalUpdateEvent() {
        final JournalChanger journalChanger = new NoActionJournalChanger();
        final NotificationSender notificationSender = new NoNotificationSender();
        return new JournalEvent(dataStorage, journalChanger, notificationSender);
    }

    private PlayerlessEvent createStaticJournalDeleteEvent(final Instruction instruction) throws QuestException {
        final Variable<JournalEntryID> entryID = instruction.get(instruction.getPart(2), JournalEntryID::new);
        return new DeleteJournalPlayerlessEvent(dataStorage, saver, profileProvider, entryID);
    }
}
