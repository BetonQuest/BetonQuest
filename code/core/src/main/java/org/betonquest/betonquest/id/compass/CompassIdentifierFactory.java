package org.betonquest.betonquest.id.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link CompassIdentifier}s.
 */
public class CompassIdentifierFactory extends DefaultIdentifierFactory<CompassIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public CompassIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager, "Compass");
    }

    @Override
    public CompassIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return requireSection(new DefaultCompassIdentifier(entry.getKey(), entry.getValue()), DefaultCompassIdentifier.COMPASS_SECTION);
    }
}
