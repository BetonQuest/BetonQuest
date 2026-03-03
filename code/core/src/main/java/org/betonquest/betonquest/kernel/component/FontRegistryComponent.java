package org.betonquest.betonquest.kernel.component;

import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.common.component.font.DefaultFontRegistry;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.lib.font.FontRetriever;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultFontRegistry}.
 */
public class FontRegistryComponent extends AbstractCoreComponent {

    /**
     * Create a new FontRegistryComponent.
     */
    public FontRegistryComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultFontRegistry.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final BetonQuestLogger log = loggerFactory.create(DefaultFontRegistry.class);

        final Key defaultkey = Key.key("default");
        final File fontFolder = new File(plugin.getDataFolder(), "fonts");
        final FontRetriever fontRetriever = new FontRetriever();
        final DefaultFontRegistry fontRegistry = new DefaultFontRegistry(defaultkey);
        plugin.saveResource("fonts/default.font.bin", true);
        final List<Pair<Key, Font>> fonts = fontRetriever.loadFonts(fontFolder.toPath());
        fonts.forEach(pair -> fontRegistry.registerFont(pair.getKey(), pair.getValue()));
        log.info("Loaded " + fonts.size() + " font index files.");
        if (fonts.isEmpty()) {
            throw new IllegalStateException("Could not load fonts!");
        }

        dependencyProvider.take(DefaultFontRegistry.class, fontRegistry);
    }
}
