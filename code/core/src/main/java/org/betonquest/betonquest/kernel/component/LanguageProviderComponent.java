package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link LanguageProvider}.
 */
public class LanguageProviderComponent extends AbstractCoreComponent {

    /**
     * Create a new LanguageProviderComponent.
     */
    public LanguageProviderComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(ConfigAccessor.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(LanguageProvider.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final ConfigAccessor config = getDependency(ConfigAccessor.class);

        final LanguageProvider languageProvider = () -> config.getString("language.default", "en-US");

        dependencyProvider.take(LanguageProvider.class, languageProvider);
    }
}
