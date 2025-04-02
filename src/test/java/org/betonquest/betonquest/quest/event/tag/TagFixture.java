package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TagFixture {

    protected List<VariableIdentifier> createIdList(final String... inputTags) throws QuestException {
        final List<VariableIdentifier> tags = new ArrayList<>();
        for (final String tag : inputTags) {
            tags.add(createId(tag));
        }
        return tags;
    }

    protected VariableIdentifier createId(final String inputTag) throws QuestException {
        final VariableIdentifier tag = mock(VariableIdentifier.class);
        when(tag.getValue(any())).thenReturn(inputTag);
        return tag;
    }
}
