package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.TagData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link DeleteTagChanger}
 */
@ExtendWith(MockitoExtension.class)
class DeleteTagChangerTest {
    @Test
    void testDeleteTagChangerRemoveNoTags(@Mock final TagData tagData) {
        final DeleteTagChanger changer = new DeleteTagChanger();

        changer.changeTags(tagData);
        verifyNoInteractions(tagData);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testDeleteTagChangerRemoveMultipleTags(@Mock final TagData tagData) {
        final String[] tags = {"tag-1", "tag-2", "tag-3"};
        final DeleteTagChanger changer = new DeleteTagChanger(tags);

        changer.changeTags(tagData);
        verify(tagData).removeTag("tag-1");
        verify(tagData).removeTag("tag-2");
        verify(tagData).removeTag("tag-3");
    }
}
