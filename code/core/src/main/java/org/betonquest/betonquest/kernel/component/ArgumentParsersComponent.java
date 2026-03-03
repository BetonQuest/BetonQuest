package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Server;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultArgumentParsers}.
 */
public class ArgumentParsersComponent extends AbstractCoreComponent {

    /**
     * Create a new ArgumentParsersComponent.
     */
    public ArgumentParsersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Server.class, ItemManager.class, ItemIdentifierFactory.class, TextParser.class, Identifiers.class,
                ParsedSectionTextCreator.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultArgumentParsers.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final ItemManager itemManager = getDependency(ItemManager.class);
        final ItemIdentifierFactory itemIdentifierFactory = getDependency(ItemIdentifierFactory.class);
        final TextParser textParser = getDependency(TextParser.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Server server = getDependency(Server.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);

        final DefaultArgumentParsers defaultArgumentParsers = new DefaultArgumentParsers(itemManager, itemIdentifierFactory, textParser, server, identifiers, parsedSectionTextCreator);

        dependencyProvider.take(DefaultArgumentParsers.class, defaultArgumentParsers);
    }
}
