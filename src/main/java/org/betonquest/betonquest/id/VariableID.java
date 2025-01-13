package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.instruction.VariableInstruction;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents variable-related identifiers in BetonQuest.
 */
public class VariableID extends ID {

    /**
     * Constructs a new VariableID with the given logger factory, quest package, and identifier.
     *
     * @param loggerFactory The factory to create custom {@link BetonQuestLogger} instance for the event.
     * @param pack          The quest package that this identifier belongs to.
     * @param identifier    The identifier string. It should start and end with '%' character.
     * @throws ObjectNotFoundException if the identifier string does not start and end with '%' character.
     */
    public VariableID(final BetonQuestLoggerFactory loggerFactory, @Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier.substring(1, identifier.length() - 1));
        if (!identifier.startsWith("%") || !identifier.endsWith("%")) {
            throw new ObjectNotFoundException("Variable instruction has to start and end with '%' characters");
        }
        super.identifier = "%" + super.identifier + "%";
        super.instruction = new VariableInstruction(loggerFactory.create(VariableInstruction.class), super.pack, this, super.identifier);
    }
}
