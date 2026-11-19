package org.betonquest.betonquest.id.particle;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;

/**
 * A default implementation for {@link ParticleIdentifier}.
 */
public class DefaultParticleIdentifier extends DefaultIdentifier implements ParticleIdentifier {

    /**
     * The section name for particles.
     */
    public static final String PARTICLES_SECTION = "particles";

    /**
     * Create a new particle identifier.
     *
     * @param pack       the quest package this identifier belongs to
     * @param identifier the identifier without the package name
     */
    public DefaultParticleIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
