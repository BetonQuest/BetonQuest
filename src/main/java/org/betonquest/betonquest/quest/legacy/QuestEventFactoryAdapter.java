package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.OnlinePlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.OnlinePlayerEvent;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link EventFactory EventFactories} create {@link QuestEvent}s from the {@link Event}s and
 * {@link StaticEvent}s they create.
 */
public class QuestEventFactoryAdapter extends LegacyFactoryAdapter<Event, StaticEvent, OnlinePlayerEvent, QuestEvent> {
    /**
     * Create the factory from an {@link EventFactory} and/or {@link StaticEventFactory}.
     * <p>
     * When no player factory is given the static/playerless factory is required.
     *
     * @param playerFactory       player event factory to use
     * @param playerlessFactory   playerless event factory to use
     * @param onlinePlayerFactory online player event factory to use
     * @throws IllegalArgumentException when no factory is given
     */
    public QuestEventFactoryAdapter(@Nullable final PlayerQuestFactory<Event> playerFactory,
                                    @Nullable final PlayerlessQuestFactory<StaticEvent> playerlessFactory,
                                    @Nullable final OnlinePlayerQuestFactory<OnlinePlayerEvent> onlinePlayerFactory) {
        super(playerFactory, playerlessFactory, onlinePlayerFactory);
    }

    @Override
    protected QuestEvent getAdapter(final Instruction instruction, @Nullable final Event playerType,
                                    @Nullable final StaticEvent playerlessType, @Nullable final OnlinePlayerEvent onlinePlayerType)
            throws InstructionParseException {
        return new QuestEventAdapter(instruction, playerType, playerlessType, onlinePlayerType);
    }
}
