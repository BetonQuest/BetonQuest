package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.database.TagData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test {@link TagAction}.
 */
@ExtendWith(MockitoExtension.class)
class TagActionTest {

    @Test
    void testTagActionUsesAlwaysActualTagData(
            @Mock final TagData firstData,
            @Mock final TagData secondData,
            @Mock final TagChanger tagChanger,
            @Mock final Profile profile1,
            @Mock final Profile profile2
    ) throws QuestException {
        final Iterator<TagData> data = List.of(firstData, secondData).iterator();
        final TagAction tagAction = new TagAction(player -> data.next(), tagChanger);

        tagAction.execute(profile1);
        tagAction.execute(profile2);

        verify(tagChanger).changeTags(firstData, profile1);
        verify(tagChanger).changeTags(secondData, profile2);
    }
}
