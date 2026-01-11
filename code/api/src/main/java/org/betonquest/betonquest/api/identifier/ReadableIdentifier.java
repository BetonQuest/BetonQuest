package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;

/**
 * An {@link Identifier} with an instruction attached.
 */
public interface ReadableIdentifier extends Identifier {

    /**
     * The section the identifier is defined in.
     *
     * @return the section
     */
    String getSection();

    /**
     * The defined instruction value as raw string.
     *
     * @return the raw instruction
     * @throws QuestException if the instruction could not be read
     */
    String readRawInstruction() throws QuestException;
}
