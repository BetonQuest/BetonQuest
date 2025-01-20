package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Adapter for {@link Event} and {@link StaticEvent} to fit the old convention of {@link QuestEvent}.
 */
public class QuestEventAdapter extends QuestEvent {
    /**
     * The player event to be adapted.
     */
    @Nullable
    private final Event event;

    /**
     * The "static"/playerless event to be adapted.
     */
    @Nullable
    private final StaticEvent staticEvent;

    /**
     * Create a quest event from an {@link Event} and a {@link StaticEvent}. If the event does not support "static"
     * execution ({@code staticness = false}) then no {@link StaticEvent} instance must be provided.
     * <p>
     * When no normal event is given the static event is required.
     *
     * @param instruction instruction used to create the events
     * @param event       event to use
     * @param staticEvent static event to use or null if no static execution is supported
     * @throws QuestException if the instruction contains errors
     */
    public QuestEventAdapter(final Instruction instruction, @Nullable final Event event, @Nullable final StaticEvent staticEvent) throws QuestException {
        super(instruction, false);
        if (event == null && staticEvent == null) {
            throw new IllegalArgumentException("Either the normal or static factory must be present!");
        }
        this.event = event;
        this.staticEvent = staticEvent;
        staticness = staticEvent != null;
        persistent = true;
    }

    @Override
    protected Void execute(@Nullable final Profile profile) throws QuestException {
        if (event == null || profile == null) {
            Objects.requireNonNull(staticEvent);
            staticEvent.execute();
        } else {
            event.execute(profile);
        }
        return null;
    }
}
