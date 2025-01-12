package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link OnlineProfileGroupStaticEventAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class OnlineProfileGroupStaticEventAdapterTest {

    /**
     * Internal non-static event mock that is adapted to a static event by the tested class.
     */
    @Mock
    private Event internalEvent;

    /**
     * Create test class instance.
     */
    public OnlineProfileGroupStaticEventAdapterTest() {
    }

    private static Stream<List<OnlineProfile>> playerListSource() {
        return Stream.of(
                List.of(createRandomProfile()),
                List.of(createRandomProfile(), createRandomProfile()),
                List.of(createRandomProfile(), createRandomProfile(), createRandomProfile(), createRandomProfile())
        );
    }

    private static OnlineProfile createRandomProfile() {
        return mock(OnlineProfile.class);
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("playerListSource")
    void testInternalEventIsExecutedForEachPlayerExactlyOnce(final List<OnlineProfile> onlineProfileList) throws QuestException {
        final OnlineProfileGroupStaticEventAdapter subject = new OnlineProfileGroupStaticEventAdapter(() -> onlineProfileList, internalEvent);
        subject.execute();

        verifyExecutedOnceForPlayers(onlineProfileList);
        verifyNoMoreInteractions(internalEvent);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testSupplierIsCalledEveryTime() throws QuestException {
        final List<OnlineProfile> firstExecution = List.of(createRandomProfile(), createRandomProfile());
        final List<OnlineProfile> secondExecution = List.of(createRandomProfile(), createRandomProfile(), createRandomProfile());
        final Iterator<List<OnlineProfile>> playerListsForSupplier = List.of(
                firstExecution, secondExecution
        ).iterator();

        final OnlineProfileGroupStaticEventAdapter subject = new OnlineProfileGroupStaticEventAdapter(playerListsForSupplier::next, internalEvent);
        subject.execute();
        verifyExecutedOnceForPlayers(firstExecution);
        verifyNotExecutedForPlayers(secondExecution);

        subject.execute();
        // the event was executed once during the first call for the first batch of players,
        // but it would be more than once if the second call did execute for the players of the first batch too
        verifyExecutedOnceForPlayers(firstExecution);
        verifyExecutedOnceForPlayers(secondExecution);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testAdapterFailsOnFirstEventFailure() throws QuestException {
        final List<OnlineProfile> playerList = List.of(createRandomProfile(), createRandomProfile(), createRandomProfile());
        final OnlineProfile firstProfile = playerList.get(0);
        final OnlineProfile failingProfile = playerList.get(1);
        final Exception eventFailureException = new QuestException("test exception");

        doNothing().when(internalEvent).execute(firstProfile);
        doThrow(eventFailureException).when(internalEvent).execute(failingProfile);

        final OnlineProfileGroupStaticEventAdapter subject = new OnlineProfileGroupStaticEventAdapter(() -> playerList, internalEvent);
        assertThrows(QuestException.class, subject::execute);
        verify(internalEvent).execute(firstProfile);
        verify(internalEvent, never()).execute(playerList.get(2));
    }

    private void verifyExecutedOnceForPlayers(final Iterable<OnlineProfile> onlineProfilesList) throws QuestException {
        for (final OnlineProfile onlineProfile : onlineProfilesList) {
            verify(internalEvent).execute(onlineProfile);
        }
    }

    private void verifyNotExecutedForPlayers(final Iterable<OnlineProfile> onlineProfilesList) throws QuestException {
        for (final OnlineProfile onlineProfile : onlineProfilesList) {
            verify(internalEvent, never()).execute(onlineProfile);
        }
    }
}
