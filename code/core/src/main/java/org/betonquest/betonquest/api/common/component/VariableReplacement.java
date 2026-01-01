package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;

/**
 * Represents a replacement in a {@link VariableComponent}.
 *
 * @param placeholder the placeholder to replace
 * @param replacement the replacement component
 */
public record VariableReplacement(String placeholder, Component replacement) {

}
