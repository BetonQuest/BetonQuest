package org.betonquest.betonquest.api.common.component.tagger;

import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

/**
 * An interface for tagging components with a specific prefix or marker.
 */
public interface ComponentTagger {
    /**
     * Prefixes the given component with the tag.
     *
     * @param original the original component to be tagged
     * @return the tagged component
     */
    Component tag(Component original);

    /**
     * Checks if the given component is tagged.
     *
     * @param component the component to check
     * @return true if the component is tagged, false otherwise
     */
    boolean isTagged(Component component);

    /**
     * Removes the tag from the given component.
     *
     * @param component the tagged component
     * @return the untagged component
     */
    Component removeTag(Component component);

    /**
     * Accepts the component if it is tagged, removes the tag, and passes the untagged component to the given consumer.
     *
     * @param component the component to check
     * @param untagged  the consumer to accept the untagged component
     * @return true if the component was tagged and accepted, false otherwise
     */
    boolean acceptIfTagged(Component component, Consumer<Component> untagged);
}
