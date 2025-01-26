package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class GlobalVariableID extends ID {
    public GlobalVariableID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "variables", "Global variable");
    }
}
