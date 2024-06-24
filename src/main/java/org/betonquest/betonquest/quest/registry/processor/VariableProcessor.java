package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.betonquest.betonquest.quest.registry.type.VariableTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores Variables and resolve them.
 */
public class VariableProcessor extends TypedQuestProcessor<VariableID, Variable> {
    /**
     * Logger Factory for new custom logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Variable Processor to store variables, resolves them and create new.
     *
     * @param log           the custom logger for this class
     * @param variableTypes the available variable types
     * @param loggerFactory the logger factory used in variable ids
     */
    public VariableProcessor(final BetonQuestLogger log, final VariableTypeRegistry variableTypes,
                             final BetonQuestLoggerFactory loggerFactory) {
        super(log, variableTypes, "Variable", "variables");
        this.loggerFactory = loggerFactory;
    }

    @Override
    public void load(final QuestPackage pack) {
        // Empty
    }

    @Override
    protected VariableID getIdentifier(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        return new VariableID(loggerFactory, pack, identifier);
    }

    /**
     * Generates new instance of a Variable. If a similar one was already
     * created, it will return it instead of creating a new one.
     *
     * @param pack        package in which the variable is defined
     * @param instruction instruction of the variable, including both % characters.
     * @return the Variable instance
     * @throws InstructionParseException when the variable parsing fails
     */
    public Variable create(@Nullable final QuestPackage pack, final String instruction)
            throws InstructionParseException {
        final VariableID variableID;
        try {
            variableID = new VariableID(loggerFactory, pack, instruction);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Could not load variable: " + e.getMessage(), e);
        }
        final Variable existingVariable = values.get(variableID);
        if (existingVariable != null) {
            return existingVariable;
        }
        final Instruction instructionVar = variableID.getInstruction();
        final LegacyTypeFactory<Variable> variableFactory = types.getFactory(instructionVar.current());
        if (variableFactory == null) {
            throw new InstructionParseException("Variable type " + instructionVar.current() + " is not registered");
        }

        final Variable variable = variableFactory.parseInstruction(instructionVar);
        values.put(variableID, variable);
        log.debug(pack, "Variable " + variableID + " loaded");
        return variable;
    }

    /**
     * Resoles the variable for specified player. If the variable is not loaded, it will load it on the main thread.
     *
     * @param pack    the {@link QuestPackage} in which the variable is defined
     * @param name    name of the variable (instruction, with % characters)
     * @param profile the {@link Profile} of the player
     * @return the value of this variable for given player
     * @throws InstructionParseException if the variable could not be created
     */
    public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws InstructionParseException {
        final Variable var;
        try {
            var = create(pack, name);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not create variable '" + name + "': " + e.getMessage(), e);
        }
        if (profile == null && !var.isStaticness()) {
            throw new InstructionParseException("Non-static variable '" + name + "' cannot be executed without a profile reference!");
        }
        return var.getValue(profile);
    }
}
