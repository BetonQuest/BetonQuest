package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.instruction.VariableInstruction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents variable-related identifiers.
 */
public class VariableID extends InstructionIdentifier {
    /**
     * The prefix and suffix used to identify variables.
     */
    public static final String VARIABLE_IDENTIFIER = "%";

    /**
     * Constructs a new VariableID with the given logger factory, quest package, and identifier.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        The quest package that this identifier belongs to.
     * @param identifier  The identifier string. It should start and end with '%' character.
     * @throws QuestException if the instruction could not be created or
     *                        if the identifier string does not start and end with '%' character.
     */
    public VariableID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(packManager, pack, identifier.substring(1, identifier.length() - 1), id -> {
            if (!identifier.startsWith(VARIABLE_IDENTIFIER) || !identifier.endsWith(VARIABLE_IDENTIFIER)) {
                throw new QuestException("Variable instruction has to start and end with '%' characters");
            }
            return new VariableInstruction(packManager, id.getPackage(), id, id.get());
        });
    }

    @Override
    public String get() {
        return VARIABLE_IDENTIFIER + super.get() + VARIABLE_IDENTIFIER;
    }
}
