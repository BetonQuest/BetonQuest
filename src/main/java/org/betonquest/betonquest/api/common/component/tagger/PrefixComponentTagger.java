package org.betonquest.betonquest.api.common.component.tagger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.List;
import java.util.function.Consumer;

/**
 * A utility class for tagging and untagging text components.
 */
public class PrefixComponentTagger implements ComponentTagger {
    /**
     * The component used for tagging messages.
     */
    private final TextComponent tagComponent;

    /**
     * Constructs a ComponentTagger with the specified tag.
     * The tag is a string, and should contain information that makes it unique enough,
     * to avoid collisions with the chat messages or {@link PrefixComponentTagger}s, and also make it easy to identify.
     *
     * @param tag the tag to use for marking messages
     */
    public PrefixComponentTagger(final String tag) {
        this.tagComponent = Component.text(tag);
    }

    @Override
    public Component tag(final Component original) {
        return tagComponent.append(original);
    }

    @Override
    public boolean isTagged(final Component component) {
        return component instanceof final TextComponent textComponent
                && tagComponent.content().equals(textComponent.content());
    }

    @Override
    public Component removeTag(final Component component) {
        final List<Component> children = component.children();
        if (children.isEmpty()) {
            return Component.empty();
        }
        return children.get(0);
    }

    @Override
    public boolean acceptIfTagged(final Component component, final Consumer<Component> untagged) {
        if (isTagged(component)) {
            untagged.accept(removeTag(component));
            return true;
        }
        return false;
    }
}
