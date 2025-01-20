package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.Utils;

import java.util.Locale;

/**
 * Factory to create global tag events from {@link Instruction}s.
 */
public class TagGlobalEventFactory implements EventFactory, StaticEventFactory {
    /**
     * BetonQuest instance to provide to events.
     */
    private final BetonQuest betonQuest;

    /**
     * Create the global tag event factory.
     *
     * @param betonQuest BetonQuest instance to pass on
     */
    public TagGlobalEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final String[] tags = getTags(instruction);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createAddTagEvent(tags);
            case "delete", "del" -> createDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final String[] tags = getTags(instruction);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createStaticAddTagEvent(tags);
            case "delete", "del" -> createStaticDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private String[] getTags(final Instruction instruction) throws QuestException {
        final String[] tags;
        tags = instruction.getArray();
        for (int ii = 0; ii < tags.length; ii++) {
            tags[ii] = Utils.addPackage(instruction.getPackage(), tags[ii]);
        }
        return tags;
    }

    private StaticEvent createStaticAddTagEvent(final String... tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new StaticTagEvent(betonQuest.getGlobalData(), tagChanger);
    }

    private StaticEvent createStaticDeleteTagEvent(final String... tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new StaticTagEvent(betonQuest.getGlobalData(), tagChanger);
    }

    private Event createAddTagEvent(final String... tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagEvent(profile -> betonQuest.getGlobalData(), tagChanger);
    }

    private Event createDeleteTagEvent(final String... tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagEvent(profile -> betonQuest.getGlobalData(), tagChanger);
    }
}
