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
 * Test {@link AddTagChanger}.
 */
@ExtendWith(MockitoExtension.class)
class AddTagChangerTest {

    @Test
    void testAddTagChangerAddNoTags(@Mock final TagHolder tagData) throws QuestException {
        final AddTagChanger changer = new AddTagChanger(new DefaultArgument<>(Collections.emptyList()));
        changer.changeTags(tagData, null);
        verifyNoInteractions(tagData);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testAddTagChangerAddMultipleTags(@Mock final TagHolder tagData) throws QuestException {
        final AddTagChanger changer = new AddTagChanger(new DefaultArgument<>(List.of("tag-1", "tag-2", "tag-3")));

        changer.changeTags(tagData, null);
        verify(tagData).add("tag-1");
        verify(tagData).add("tag-2");
        verify(tagData).add("tag-3");
    }
}
