package org.betonquest.betonquest.id.npc;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;

/**
 * The default implementation for {@link NpcIdentifier}s.
 */
public class DefaultNpcIdentifier extends DefaultReadableIdentifier implements NpcIdentifier {

    /**
     * The section in the configuration where npcs are defined.
     */
    public static final String NPC_SECTION = "npcs";

    /**
     * Creates a new npc identifier.
     *
     * @param pack       the package of the npc.
     * @param identifier the identifier of the npc.
     */
    protected DefaultNpcIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier, NPC_SECTION);
    }
}
