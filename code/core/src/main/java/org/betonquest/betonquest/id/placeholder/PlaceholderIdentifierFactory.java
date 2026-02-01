package org.betonquest.betonquest.id.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link PlaceholderIdentifier}s.
 */
public class PlaceholderIdentifierFactory extends DefaultIdentifierFactory<PlaceholderIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public PlaceholderIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager, "Placeholder");
    }

    @Override
    public PlaceholderIdentifier parseIdentifier(@Nullable final QuestPackage source, final String identifier) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, identifier.substring(1, identifier.length() - 1));
        return new DefaultPlaceholderIdentifier(entry.getKey(), entry.getValue(), identifier);
    }
}
