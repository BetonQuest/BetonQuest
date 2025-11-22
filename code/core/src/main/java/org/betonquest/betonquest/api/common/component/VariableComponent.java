package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;

/**
 * Represents a component with variable to replace.
 *
 * @param component the component to replace variables in
 */
public record VariableComponent(Component component) {

    /**
     * Replaces the variables in the component with the given replacements.
     *
     * @param variables the replacements
     * @return the component with replaced variables
     */
    public Component resolve(final VariableReplacement... variables) {
        Component component = this.component;
        for (final VariableReplacement variable : variables) {
            component = component.replaceText(builder -> builder.matchLiteral("{" + variable.variable() + "}").replacement(variable.replacement()));
        }
        return component;
    }
}
