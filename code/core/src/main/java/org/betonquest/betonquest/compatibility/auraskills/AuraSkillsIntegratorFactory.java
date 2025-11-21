package org.betonquest.betonquest.compatibility.auraskills;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;

/**
 * Factory for creating {@link AuraSkillsIntegrator} instances.
 */
public class AuraSkillsIntegratorFactory implements IntegratorFactory {
    /**
     * Creates a new instance of the factory.
     */
    public AuraSkillsIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator() {
        return new AuraSkillsIntegrator();
    }
}
