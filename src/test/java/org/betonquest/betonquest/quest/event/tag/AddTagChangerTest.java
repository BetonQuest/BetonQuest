package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.TagData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link AddTagChanger}
 */
@ExtendWith(MockitoExtension.class)
class AddTagChangerTest {
    @Test
    void testAddTagChangerAddNoTags(@Mock final TagData tagData) {
        final AddTagChanger changer = new AddTagChanger();

        changer.changeTags(tagData);
        verifyNoInteractions(tagData);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testAddTagChangerAddMultipleTags(@Mock final TagData tagData) {
        final String[] tags = {"tag-1", "tag-2", "tag-3"};
        final AddTagChanger changer = new AddTagChanger(tags);

        changer.changeTags(tagData);
        verify(tagData).addTag("tag-1");
        verify(tagData).addTag("tag-2");
        verify(tagData).addTag("tag-3");
    }
}
