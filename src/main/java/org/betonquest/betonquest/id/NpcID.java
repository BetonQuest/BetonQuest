package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies a {@link org.betonquest.betonquest.api.quest.npc.Npc Npc} via the path syntax.
 * Handles relative and absolute paths.
 */
public class NpcID extends ID {

    /**
     * Creates a new Npc id.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @throws QuestException when the npc could not be resolved with the given identifier
     */
    public NpcID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "npcs", "Npc");
    }
}
