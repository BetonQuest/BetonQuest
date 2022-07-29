package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link OnlinePlayerGroupStaticEventAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class OnlinePlayerGroupStaticEventAdapterTest {

    /**
     * Internal non-static event mock that is adapted to a static event by the tested class.
     */
    @Mock
    private Event internalEvent;

    /**
     * Create test class instance.
     */
    public OnlinePlayerGroupStaticEventAdapterTest() {
    }

    static Stream<List<Player>> playerListSource() {
        return Stream.of(
                List.of(createRandomPlayer()),
                List.of(createRandomPlayer(), createRandomPlayer()),
                List.of(createRandomPlayer(), createRandomPlayer(), createRandomPlayer(), createRandomPlayer())
        );
    }

    static Player createRandomPlayer() {
        final Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        return player;
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("playerListSource")
    void testInternalEventIsExecutedForEachPlayerExactlyOnce(final List<Player> playerList) throws QuestRuntimeException {
        final OnlinePlayerGroupStaticEventAdapter subject = new OnlinePlayerGroupStaticEventAdapter(() -> playerList, internalEvent);
        subject.execute();

        verifyExecutedOnceForPlayers(playerList);
        verifyNoMoreInteractions(internalEvent);
    }

    @Test
    void testSupplierIsCalledEveryTime() throws QuestRuntimeException {
        final List<Player> firstExecution = List.of(createRandomPlayer(), createRandomPlayer());
        final List<Player> secondExecution = List.of(createRandomPlayer(), createRandomPlayer(), createRandomPlayer());
        final Iterator<List<Player>> playerListsForSupplier = List.of(
                firstExecution, secondExecution
        ).iterator();

        final OnlinePlayerGroupStaticEventAdapter subject = new OnlinePlayerGroupStaticEventAdapter(playerListsForSupplier::next, internalEvent);
        subject.execute();
        verifyExecutedOnceForPlayers(firstExecution);
        verifyNotExecutedForPlayers(secondExecution);

        subject.execute();
        // the event was executed once during the first call for the first batch of players,
        // but it would be more than once if the second call did execute for the players of the first batch too
        verifyExecutedOnceForPlayers(firstExecution);
        verifyExecutedOnceForPlayers(secondExecution);
    }

    @Test
    void testAdapterFailsOnFirstEventFailure() throws QuestRuntimeException {
        final List<Player> playerList = List.of(createRandomPlayer(), createRandomPlayer(), createRandomPlayer());
        final Profile firstProfile = PlayerConverter.getID(playerList.get(0));
        final Profile failingProfile = PlayerConverter.getID(playerList.get(1));
        final Exception eventFailureException = new QuestRuntimeException("test exception");

        doNothing().when(internalEvent).execute(firstProfile);
        doThrow(eventFailureException).when(internalEvent).execute(failingProfile);

        final OnlinePlayerGroupStaticEventAdapter subject = new OnlinePlayerGroupStaticEventAdapter(() -> playerList, internalEvent);
        assertThrows(QuestRuntimeException.class, subject::execute);
        verify(internalEvent).execute(firstProfile);
        verify(internalEvent, never()).execute(PlayerConverter.getID(playerList.get(2)));
    }

    private void verifyExecutedOnceForPlayers(final Iterable<Player> playerList) throws QuestRuntimeException {
        for (final Player player : playerList) {
            verify(internalEvent).execute(PlayerConverter.getID(player));
        }
    }

    private void verifyNotExecutedForPlayers(final Iterable<Player> playerList) throws QuestRuntimeException {
        for (final Player player : playerList) {
            verify(internalEvent, never()).execute(PlayerConverter.getID(player));
        }
    }
}
