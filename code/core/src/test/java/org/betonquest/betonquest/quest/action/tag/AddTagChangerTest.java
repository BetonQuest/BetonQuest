package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.database.TagData;
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
    void testAddTagChangerAddNoTags(@Mock final TagData tagData) throws QuestException {
        final AddTagChanger changer = new AddTagChanger(new DefaultArgument<>(Collections.emptyList()));
        changer.changeTags(tagData, null);
        verifyNoInteractions(tagData);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testAddTagChangerAddMultipleTags(@Mock final TagData tagData) throws QuestException {
        final AddTagChanger changer = new AddTagChanger(new DefaultArgument<>(List.of("tag-1", "tag-2", "tag-3")));

        changer.changeTags(tagData, null);
        verify(tagData).addTag("tag-1");
        verify(tagData).addTag("tag-2");
        verify(tagData).addTag("tag-3");
    }
}
