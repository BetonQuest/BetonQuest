package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link EventFactory EventFactories} create {@link QuestEvent}s from the {@link Event}s and
 * {@link StaticEvent}s they create.
 */
public class QuestEventFactoryAdapter extends LegacyFactoryAdapter<Event, StaticEvent, QuestEvent> {
    /**
     * Create the factory from an {@link EventFactory} and/or {@link StaticEventFactory}.
     * <p>
     * When no player factory is given the static/playerless factory is required.
     *
     * @param playerFactory     player factory to use
     * @param playerlessFactory playerless event factory to use
     */
    public QuestEventFactoryAdapter(@Nullable final PlayerQuestFactory<Event> playerFactory,
                                    @Nullable final PlayerlessQuestFactory<StaticEvent> playerlessFactory) {
        super(playerFactory, playerlessFactory);
    }

    @Override
    protected QuestEvent getAdapter(final Instruction instruction, @Nullable final Event playerType, @Nullable final StaticEvent playerlessType)
            throws QuestException {
        return new QuestEventAdapter(instruction, playerType, playerlessType);
    }
}
