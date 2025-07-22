package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
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
 * Test {@link OnlineProfileGroupPlayerlessEventAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class OnlineProfileGroupPlayerlessEventAdapterTest {

    /**
     * Internal non-static event mock that is adapted to a static event by the tested class.
     */
    @Mock
    private PlayerEvent internalPlayerEvent;

    /**
     * Create test class instance.
     */
    public OnlineProfileGroupPlayerlessEventAdapterTest() {
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
        final OnlineProfileGroupPlayerlessEventAdapter subject = new OnlineProfileGroupPlayerlessEventAdapter(() -> onlineProfileList, internalPlayerEvent);
        subject.execute();

        verifyExecutedOnceForPlayers(onlineProfileList);
        verifyNoMoreInteractions(internalPlayerEvent);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testSupplierIsCalledEveryTime() throws QuestException {
        final List<OnlineProfile> firstExecution = List.of(createRandomProfile(), createRandomProfile());
        final List<OnlineProfile> secondExecution = List.of(createRandomProfile(), createRandomProfile(), createRandomProfile());
        final Iterator<List<OnlineProfile>> playerListsForSupplier = List.of(
                firstExecution, secondExecution
        ).iterator();

        final OnlineProfileGroupPlayerlessEventAdapter subject = new OnlineProfileGroupPlayerlessEventAdapter(playerListsForSupplier::next, internalPlayerEvent);
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

        doNothing().when(internalPlayerEvent).execute(firstProfile);
        doThrow(eventFailureException).when(internalPlayerEvent).execute(failingProfile);

        final OnlineProfileGroupPlayerlessEventAdapter subject = new OnlineProfileGroupPlayerlessEventAdapter(() -> playerList, internalPlayerEvent);
        assertThrows(QuestException.class, subject::execute);
        verify(internalPlayerEvent).execute(firstProfile);
        verify(internalPlayerEvent, never()).execute(playerList.get(2));
    }

    private void verifyExecutedOnceForPlayers(final Iterable<OnlineProfile> onlineProfilesList) throws QuestException {
        for (final OnlineProfile onlineProfile : onlineProfilesList) {
            verify(internalPlayerEvent).execute(onlineProfile);
        }
    }

    private void verifyNotExecutedForPlayers(final Iterable<OnlineProfile> onlineProfilesList) throws QuestException {
        for (final OnlineProfile onlineProfile : onlineProfilesList) {
            verify(internalPlayerEvent, never()).execute(onlineProfile);
        }
    }
}
