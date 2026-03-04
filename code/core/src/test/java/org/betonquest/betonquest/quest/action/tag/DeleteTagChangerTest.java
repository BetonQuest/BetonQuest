package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.TagHolder;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test {@link DeleteTagChanger}.
 */
@ExtendWith(MockitoExtension.class)
class DeleteTagChangerTest {

    @Test
    void testDeleteTagChangerRemoveNoTags(@Mock final TagHolder tagData) throws QuestException {
        final DeleteTagChanger changer = new DeleteTagChanger(new DefaultArgument<>(Collections.emptyList()));

        changer.changeTags(tagData, null);
        verifyNoInteractions(tagData);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testDeleteTagChangerRemoveMultipleTags(@Mock final TagHolder tagData) throws QuestException {
        final DeleteTagChanger changer = new DeleteTagChanger(new DefaultArgument<>(List.of("tag-1", "tag-2", "tag-3")));

        changer.changeTags(tagData, null);
        verify(tagData).remove("tag-1");
        verify(tagData).remove("tag-2");
        verify(tagData).remove("tag-3");
    }
}
