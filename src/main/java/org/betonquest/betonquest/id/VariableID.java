package org.betonquest.betonquest.id;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents variable-related identifiers in BetonQuest.
 */
public class VariableID extends ID {
    /**
     * Factory to create custom {@link BetonQuestLogger} instance for the event.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Constructs a new VariableID with the given logger factory, quest package, and identifier.
     *
     * @param loggerFactory The factory to create custom {@link BetonQuestLogger} instance for the event.
     * @param pack          The quest package that this identifier belongs to.
     * @param identifier    The identifier string. It should start and end with '%' character.
     * @throws ObjectNotFoundException if the identifier string does not start and end with '%' character.
     */
    public VariableID(final BetonQuestLoggerFactory loggerFactory, @Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier.replaceAll("%", ""));
        this.loggerFactory = loggerFactory;
        if (!super.identifier.isEmpty() && identifier.charAt(0) != '%' && !identifier.endsWith("%")) {
            throw new ObjectNotFoundException("Variable instruction has to start and end with '%' characters");
        }
        super.rawInstruction = identifier;
        super.identifier = "%" + super.identifier + "%";
    }

    @NotNull
    @Override
    public Instruction generateInstruction() {
        return new VariableInstruction(loggerFactory.create(VariableInstruction.class), super.pack, this, super.identifier);
    }

    @Override
    public String getFullID() {
        return pack.getQuestPath() + "-" + getBaseID();
    }

}
