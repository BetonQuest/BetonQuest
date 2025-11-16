package org.betonquest.betonquest.api.common.component.tagger;

import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

/**
 * A ComponentTagger implementation that does not tag components.
 * It simply returns the original component for tagging and indicates that no component is tagged.
 */
public class NoneComponentTagger implements ComponentTagger {
    /**
     * Constructs a NoneComponentTagger instance.
     */
    public NoneComponentTagger() {
    }

    @Override
    public Component tag(final Component original) {
        return original;
    }

    @Override
    public boolean isTagged(final Component component) {
        return false;
    }

    @Override
    public Component removeTag(final Component component) {
        return component;
    }

    @Override
    public boolean acceptIfTagged(final Component component, final Consumer<Component> untagged) {
        return false;
    }
}
