package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies a {@link org.betonquest.betonquest.api.quest.npc.Npc Npc} via the path syntax.
 */
public class NpcID extends InstructionIdentifier {

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
