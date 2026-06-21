package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;

/**
 * An {@link Identifier} with an instruction attached.
 *
 * @since 3.0.0
 */
public interface ReadableIdentifier extends Identifier {

    /**
     * The section the identifier is defined in.
     *
     * @return the name of the section
     * @since 3.0.0
     */
    String getSection();

    /**
     * The defined instruction value as raw string.
     *
     * @return the raw instruction
     * @throws QuestException if the instruction could not be read
     * @since 3.0.0
     */
    String readRawInstruction() throws QuestException;
}
