package org.betonquest.betonquest.id.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link NpcIdentifier}s.
 */
public class NpcIdentifierFactory extends DefaultIdentifierFactory<NpcIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public NpcIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public NpcIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultNpcIdentifier identifier = new DefaultNpcIdentifier(entry.getKey(), entry.getValue());
        return requireInstruction(identifier, DefaultNpcIdentifier.NPC_SECTION);
    }
}
