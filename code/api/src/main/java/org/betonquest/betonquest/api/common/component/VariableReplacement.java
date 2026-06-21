package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;

/**
 * Represents a replacement in a variable component.
 *
 * @param placeholder the placeholder to replace
 * @param replacement the replacement component
 * @since 3.0.0
 */
public record VariableReplacement(String placeholder, Component replacement) {

}
