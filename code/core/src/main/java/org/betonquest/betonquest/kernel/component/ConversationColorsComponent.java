package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ConversationColors}.
 */
public class ConversationColorsComponent extends AbstractCoreComponent {

    /**
     * Create a new ConversationColorsComponent.
     */
    public ConversationColorsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(FileConfigAccessor.class, TextParser.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ConversationColors.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final FileConfigAccessor config = getDependency(FileConfigAccessor.class);
        final TextParser textParser = getDependency(TextParser.class);
        final Reloader reloader = getDependency(Reloader.class);

        final ConversationColors conversationColors = new ConversationColors(textParser, config);

        dependencyProvider.take(ConversationColors.class, conversationColors);
        reloader.register(ReloadPhase.PACKAGES, conversationColors::load);
    }
}
