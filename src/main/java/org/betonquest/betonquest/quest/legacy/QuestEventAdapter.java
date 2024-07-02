package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.OnlinePlayerEvent;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
     * The online player event to be adapted.
     */
    @Nullable
    private final OnlinePlayerEvent onlinePlayerType;

    /**
     * Create a quest event from an {@link Event} and a {@link StaticEvent}. If the event does not support "static"
     * execution ({@code staticness = false}) then no {@link StaticEvent} instance must be provided.
     *
     * @param instruction       instruction used to create the events
     * @param event             event to use
     * @param staticEvent       static event to use or null if no static execution is supported
     * @param onlinePlayerEvent online player event to use
     * @throws InstructionParseException if the instruction contains errors
     * @throws IllegalArgumentException  when no event is given
     */
    public QuestEventAdapter(final Instruction instruction, @Nullable final Event event, @Nullable final StaticEvent staticEvent,
                             @Nullable final OnlinePlayerEvent onlinePlayerEvent) throws InstructionParseException {
        super(instruction, false);
        if (event == null && staticEvent == null && onlinePlayerEvent == null) {
            throw new IllegalArgumentException("At least one event must be present!");
        }
        this.event = event;
        this.staticEvent = staticEvent;
        this.onlinePlayerType = onlinePlayerEvent;
        staticness = staticEvent != null;
        persistent = event != null;
    }

    @Override
    protected Void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        if (onlinePlayerType != null && profile != null) {
            final Optional<OnlineProfile> optional = profile.getOnlineProfile();
            if (optional.isPresent()) {
                onlinePlayerType.execute(optional.get());
                return null;
            }
        }
        if (event != null && profile != null) {
            event.execute(profile);
            return null;
        }
        if (staticEvent != null) {
            staticEvent.execute();
            return null;
        }
        throw new QuestRuntimeException("Invalid profile for non-static event!");
    }
}
