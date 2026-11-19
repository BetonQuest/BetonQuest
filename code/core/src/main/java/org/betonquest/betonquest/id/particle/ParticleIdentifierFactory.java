package org.betonquest.betonquest.id.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ParticleIdentifier}s.
 */
public class ParticleIdentifierFactory extends DefaultIdentifierFactory<ParticleIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ParticleIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager, "Particle");
    }

    @Override
    public ParticleIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultParticleIdentifier identifier = new DefaultParticleIdentifier(entry.getKey(), entry.getValue());
        return requireSection(identifier, DefaultParticleIdentifier.PARTICLES_SECTION);
    }
}
