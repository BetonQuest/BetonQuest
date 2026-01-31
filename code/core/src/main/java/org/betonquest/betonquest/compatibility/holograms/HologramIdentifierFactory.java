package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link HologramIdentifier}s.
 */
public class HologramIdentifierFactory extends DefaultIdentifierFactory<HologramIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public HologramIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public HologramIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return new DefaultHologramIdentifier(entry.getKey(), entry.getValue());
    }
}
