package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.quest.event.DatabaseSaverPlayerlessEvent;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessEvent;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupPlayerlessEventAdapter;
import org.betonquest.betonquest.quest.event.SequentialPlayerlessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Factory to create tag events from {@link Instruction}s.
 */
public class TagPlayerEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The saver to inject into database-using events.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the tag player event factory.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           database saver to use
     * @param profileProvider the profile provider instance
     */
    public TagPlayerEventFactory(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final List<VariableIdentifier> tags = instruction.getList(VariableIdentifier::new);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createAddTagEvent(tags);
            case "delete", "del" -> createDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final List<VariableIdentifier> tags = instruction.getList(VariableIdentifier::new);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> new DoNothingPlayerlessEvent();
            case "delete", "del" -> createStaticDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private TagEvent createAddTagEvent(final List<VariableIdentifier> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagEvent(dataStorage::getOffline, tagChanger);
    }

    private TagEvent createDeleteTagEvent(final List<VariableIdentifier> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagEvent(dataStorage::getOffline, tagChanger);
    }

    private PlayerlessEvent createStaticDeleteTagEvent(final List<VariableIdentifier> tags) {
        final TagEvent deleteTagEvent = createDeleteTagEvent(tags);
        final List<PlayerlessEvent> playerlessEvents = new ArrayList<>(tags.size() + 1);
        playerlessEvents.add(new OnlineProfileGroupPlayerlessEventAdapter(profileProvider::getOnlineProfiles, deleteTagEvent));
        for (final VariableIdentifier tag : tags) {
            playerlessEvents.add(new DatabaseSaverPlayerlessEvent(saver, () -> new Saver.Record(UpdateType.REMOVE_ALL_TAGS, tag.getValue(null))));
        }
        return new SequentialPlayerlessEvent(playerlessEvents.toArray(new PlayerlessEvent[0]));
    }
}
