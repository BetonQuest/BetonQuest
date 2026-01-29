package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;

/**
 * The default implementation for {@link HologramIdentifier}s.
 */
public class DefaultHologramIdentifier extends DefaultIdentifier implements HologramIdentifier {

    /**
     * Creates a new identifier without resolving the package.
     *
     * @param pack       the package the object is in
     * @param identifier the identifier of the object without the package name
     */
    protected DefaultHologramIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
