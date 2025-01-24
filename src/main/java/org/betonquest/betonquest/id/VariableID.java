package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.instruction.VariableInstruction;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents variable-related identifiers in BetonQuest.
 */
public class VariableID extends ID {

    /**
     * Constructs a new VariableID with the given logger factory, quest package, and identifier.
     *
     * @param pack       The quest package that this identifier belongs to.
     * @param identifier The identifier string. It should start and end with '%' character.
     * @throws ObjectNotFoundException if the identifier string does not start and end with '%' character.
     * @throws QuestException          if the instruction could not be created.
     */
    public VariableID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException, QuestException {
        super(pack, identifier.substring(1, identifier.length() - 1));
        if (!identifier.startsWith("%") || !identifier.endsWith("%")) {
            throw new ObjectNotFoundException("Variable instruction has to start and end with '%' characters");
        }
        super.identifier = "%" + super.identifier + "%";
        super.instruction = new VariableInstruction(super.pack, this, super.identifier);
    }
}
