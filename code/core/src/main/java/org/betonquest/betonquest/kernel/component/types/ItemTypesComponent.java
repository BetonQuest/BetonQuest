package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.item.LoreConsumer;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemHandlerRegistry;
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
        return Set.of(ConfigAccessor.class, Localizations.class, ItemRegistry.class, FontRegistry.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(SimpleQuestItemHandlerRegistry.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Localizations localizations = getDependency(Localizations.class);
        final ItemRegistry itemRegistry = getDependency(ItemRegistry.class);
        final FontRegistry fontRegistry = getDependency(FontRegistry.class);

        final BookPageWrapper bookPageWrapper = new BookPageWrapper(fontRegistry, 114, 14);

        final SimpleQuestItemHandlerRegistry handlerRegistry = new SimpleQuestItemHandlerRegistry(bookPageWrapper, new LoreConsumer.SupplierArgument(
                () -> config.getBoolean("item.quest.lore") ? localizations : null
        ));

        dependencyProvider.take(SimpleQuestItemHandlerRegistry.class, handlerRegistry);

        itemRegistry.register("simple", new SimpleQuestItemFactory(handlerRegistry), true);
        itemRegistry.registerSerializer("simple", new SimpleQuestItemSerializer(handlerRegistry));
    }
}
