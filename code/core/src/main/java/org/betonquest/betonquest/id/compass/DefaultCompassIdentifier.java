package org.betonquest.betonquest.id.compass;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.instruction.argument.parser.PackageIdentifierParser;

/**
 * The default implementation for {@link CompassIdentifier}s.
 */
public class DefaultCompassIdentifier extends DefaultIdentifier implements CompassIdentifier {

    /**
     * Creates a new compass identifier.
     *
     * @param pack       the package the identifier is related to
     * @param identifier the identifier of the compass
     */
    protected DefaultCompassIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }

    @Override
    public String getTag() {
        return PackageIdentifierParser.INSTANCE.apply(getPackage(), "compass-" + get());
    }
}
