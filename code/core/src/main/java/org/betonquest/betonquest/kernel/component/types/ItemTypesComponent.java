package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemSerializer;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading item types.
 */
public class ItemTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new ItemTypesComponent.
     */
    public ItemTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, ConfigAccessor.class,
                PluginMessage.class, TextParser.class,
                ItemRegistry.class, FontRegistry.class, PlaceholderManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final TextParser textParser = getDependency(TextParser.class);
        final ItemRegistry itemRegistry = getDependency(ItemRegistry.class);
        final FontRegistry fontRegistry = getDependency(FontRegistry.class);
        final PlaceholderManager placeholders = getDependency(PlaceholderManager.class);

        final BookPageWrapper bookPageWrapper = new BookPageWrapper(fontRegistry, 114, 14);
        itemRegistry.register("simple", new SimpleQuestItemFactory(placeholders, packManager, textParser, bookPageWrapper,
                () -> config.getBoolean("item.quest.lore") ? pluginMessage : null));
        itemRegistry.registerSerializer("simple", new SimpleQuestItemSerializer(textParser, bookPageWrapper));
    }
}
