package org.betonquest.betonquest.quest.event.language;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create language events from {@link Instruction}s.
 */
public class LanguageEventFactory implements EventFactory {

    /**
     * The Betonquest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Create the language event factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public LanguageEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String language = instruction.next();
        return new LanguageEvent(language, betonQuest);
    }
}
