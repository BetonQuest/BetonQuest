package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.function.Consumer;

/**
 * A utility class for tagging and untagging text components.
 */
public class ComponentTagger {
    /**
     * The component used for tagging messages.
     */
    private final TextComponent tagComponent;

    /**
     * Constructs a ComponentTagger with the specified tag.
     * The tag is a string, and should contain information that makes it unique enough,
     * to avoid collisions with the chat messages or {@link ComponentTagger}s, and also make it easy to identify.
     *
     * @param tag the tag to use for marking messages
     */
    public ComponentTagger(final String tag) {
        this.tagComponent = Component.text(tag);
    }

    /**
     * Prefixes the given component with the tag.
     *
     * @param original the original component to be tagged
     * @return the tagged component
     */
    public Component tag(final Component original) {
        return tagComponent.append(original);
    }

    /**
     * Checks if the given component is tagged.
     *
     * @param component the component to check
     * @return true if the component is tagged, false otherwise
     */
    public boolean isTagged(final Component component) {
        return component instanceof final TextComponent textComponent
                && tagComponent.content().equals(textComponent.content());
    }

    /**
     * Removes the tag from the given component.
     *
     * @param component the tagged component
     * @return the untagged component
     */
    public Component removeTag(final Component component) {
        return component.children().get(0);
    }

    /**
     * Accepts the component if it is tagged, removes the tag, and passes the untagged component to the given consumer.
     *
     * @param component the component to check
     * @param untagged  the consumer to accept the untagged component
     * @return true if the component was tagged and accepted, false otherwise
     */
    public boolean acceptIfTagged(final Component component, final Consumer<Component> untagged) {
        if (isTagged(component)) {
            untagged.accept(removeTag(component));
            return true;
        }
        return false;
    }
}
