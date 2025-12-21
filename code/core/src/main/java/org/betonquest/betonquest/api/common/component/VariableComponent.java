package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;

/**
 * Represents a component with a placeholder to replace.
 *
 * @param component the component to replace placeholders in
 */
public record VariableComponent(Component component) {

    /**
     * Replaces the placeholders in the component with the given replacements.
     *
     * @param replacements the replacements
     * @return the component with replaced placeholders
     */
    public Component resolve(final VariableReplacement... replacements) {
        Component component = this.component;
        for (final VariableReplacement replacement : replacements) {
            component = component.replaceText(builder -> builder.matchLiteral("{" + replacement.placeholder() + "}").replacement(replacement.replacement()));
        }
        return component;
    }
}
