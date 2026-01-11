package org.betonquest.betonquest.compatibility.effectlib.identifier;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;

/**
 * A default implementation for {@link ParticleIdentifier}.
 */
public class DefaultParticleIdentifier extends DefaultIdentifier implements ParticleIdentifier {

    /**
     * Create a new particle identifier.
     *
     * @param pack       the quest package this identifier belongs to
     * @param identifier the identifier without the package name
     */
    protected DefaultParticleIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
