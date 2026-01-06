package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.quest.action.DoNothingPlayerlessAction;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.NotificationSender;

import java.time.InstantSource;
import java.util.Locale;

/**
 * Factory to create journal events from {@link Instruction}s.
 */
public class JournalActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

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
    public JournalActionFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage, final PlayerDataStorage dataStorage, final InstantSource instantSource, final Saver saver, final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.dataStorage = dataStorage;
        this.instantSource = instantSource;
        this.saver = saver;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "update" -> createJournalUpdateEvent();
            case "add" -> createJournalAddEvent(instruction);
            case "delete" -> createJournalDeleteEvent(instruction);
            default -> throw new QuestException("Unknown journal action: " + action);
        };
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "update", "add" -> new DoNothingPlayerlessAction();
            case "delete" -> createStaticJournalDeleteEvent(instruction);
            default -> throw new QuestException("Unknown journal action: " + action);
        };
    }

    private JournalAction createJournalDeleteEvent(final Instruction instruction) throws QuestException {
        final Argument<JournalEntryID> entryID = instruction.chainForArgument(instruction.getPart(2)).parse(JournalEntryID::new).get();
        final JournalChanger journalChanger = new RemoveEntryJournalChanger(entryID);
        final NotificationSender notificationSender = new NoNotificationSender();
        return new JournalAction(dataStorage, journalChanger, notificationSender);
    }

    private JournalAction createJournalAddEvent(final Instruction instruction) throws QuestException {
        final Argument<JournalEntryID> entryID = instruction.chainForArgument(instruction.getPart(2)).parse(JournalEntryID::new).get();
        final JournalChanger journalChanger = new AddEntryJournalChanger(instantSource, entryID);
        final NotificationSender notificationSender = new IngameNotificationSender(loggerFactory.create(JournalAction.class),
                pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.INFO, "new_journal_entry");
        return new JournalAction(dataStorage, journalChanger, notificationSender);
    }

    private JournalAction createJournalUpdateEvent() {
        final JournalChanger journalChanger = new NoOperationJournalChanger();
        final NotificationSender notificationSender = new NoNotificationSender();
        return new JournalAction(dataStorage, journalChanger, notificationSender);
    }

    private PlayerlessAction createStaticJournalDeleteEvent(final Instruction instruction) throws QuestException {
        final Argument<JournalEntryID> entryID = instruction.chainForArgument(instruction.getPart(2)).parse(JournalEntryID::new).get();
        return new DeleteJournalPlayerlessAction(dataStorage, saver, profileProvider, entryID);
    }
}
