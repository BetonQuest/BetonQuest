package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.DatabaseSaverStaticEvent;
import org.betonquest.betonquest.quest.event.DoNothingStaticEvent;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.SequentialStaticEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Factory to create tag events from {@link Instruction}s.
 */
public class TagPlayerEventFactory implements EventFactory, StaticEventFactory {
    /**
     * BetonQuest instance to provide to events.
     */
    private final BetonQuest betonQuest;

    /**
     * The saver to inject into database-using events.
     */
    private final Saver saver;

    /**
     * Create the tag player event factory.
     *
     * @param betonQuest BetonQuest instance to pass on
     * @param saver      database saver to use
     */
    public TagPlayerEventFactory(final BetonQuest betonQuest, final Saver saver) {
        this.betonQuest = betonQuest;
        this.saver = saver;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String action = instruction.next();
        final String[] tags = getTags(instruction);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createAddTagEvent(tags);
            case "delete", "del" -> createDeleteTagEvent(tags);
            default -> throw new InstructionParseException("Unknown tag action: " + action);
        };
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        final String action = instruction.next();
        final String[] tags = getTags(instruction);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> new DoNothingStaticEvent();
            case "delete", "del" -> createStaticDeleteTagEvent(tags);
            default -> throw new InstructionParseException("Unknown tag action: " + action);
        };
    }

    private String[] getTags(final Instruction instruction) throws InstructionParseException {
        final String[] tags;
        tags = instruction.getArray();
        for (int ii = 0; ii < tags.length; ii++) {
            tags[ii] = Utils.addPackage(instruction.getPackage(), tags[ii]);
        }
        return tags;
    }

    private TagEvent createAddTagEvent(final String... tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagEvent(betonQuest::getOfflinePlayerData, tagChanger);
    }

    private TagEvent createDeleteTagEvent(final String... tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagEvent(betonQuest::getOfflinePlayerData, tagChanger);
    }

    private StaticEvent createStaticDeleteTagEvent(final String... tags) {
        final TagEvent deleteTagEvent = createDeleteTagEvent(tags);
        final List<StaticEvent> staticEvents = new ArrayList<>(tags.length + 1);
        staticEvents.add(new OnlineProfileGroupStaticEventAdapter(PlayerConverter::getOnlineProfiles, deleteTagEvent));
        for (final String tag : tags) {
            staticEvents.add(new DatabaseSaverStaticEvent(saver, () -> new Saver.Record(UpdateType.REMOVE_ALL_TAGS, tag)));
        }
        return new SequentialStaticEvent(staticEvents.toArray(new StaticEvent[0]));
    }
}
