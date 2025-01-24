package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.registry.type.TypeFactory;
import org.betonquest.betonquest.quest.registry.type.VariableTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores Variables and resolve them.
 */
public class VariableProcessor extends TypedQuestProcessor<VariableID, Variable> {

    /**
     * Create a new Variable Processor to store variables, resolves them and create new.
     *
     * @param log           the custom logger for this class
     * @param variableTypes the available variable types
     */
    public VariableProcessor(final BetonQuestLogger log, final VariableTypeRegistry variableTypes) {
        super(log, variableTypes, "Variable", "variables");
    }

    @Override
    public void load(final QuestPackage pack) {
        // Empty
    }

    @Override
    protected VariableID getIdentifier(final QuestPackage pack, final String identifier) throws ObjectNotFoundException, QuestException {
        return new VariableID(pack, identifier);
    }

    /**
     * Generates new instance of a Variable. If a similar one was already
     * created, it will return it instead of creating a new one.
     *
     * @param pack        package in which the variable is defined
     * @param instruction instruction of the variable, including both % characters.
     * @return the Variable instance
     * @throws QuestException when the variable parsing fails
     */
    public Variable create(@Nullable final QuestPackage pack, final String instruction)
            throws QuestException {
        final VariableID variableID;
        try {
            variableID = new VariableID(pack, instruction);
        } catch (final ObjectNotFoundException e) {
            throw new QuestException("Could not load variable: " + e.getMessage(), e);
        }
        final Variable existingVariable = values.get(variableID);
        if (existingVariable != null) {
            return existingVariable;
        }
        final Instruction instructionVar = variableID.getInstruction();
        final TypeFactory<Variable> variableFactory = types.getFactory(instructionVar.current());
        if (variableFactory == null) {
            throw new QuestException("Variable type " + instructionVar.current() + " is not registered");
        }

        final Variable variable = variableFactory.parseInstruction(instructionVar);
        values.put(variableID, variable);
        log.debug(pack, "Variable " + variableID + " loaded");
        return variable;
    }

    /**
     * Resolves the variable for specified player. If the variable is not loaded, it will create it.
     *
     * @param pack    the {@link QuestPackage} in which the variable is defined
     * @param name    name of the variable (instruction, with % characters)
     * @param profile the {@link Profile} of the player
     * @return the value of this variable for given player
     * @throws QuestException if the variable could not be created
     */
    public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws QuestException {
        final Variable var;
        try {
            var = create(pack, name);
        } catch (final QuestException e) {
            throw new QuestException("Could not create variable '" + name + "': " + e.getMessage(), e);
        }
        if (profile == null && !var.isStaticness()) {
            throw new QuestException("Non-static variable '" + name + "' cannot be executed without a profile reference!");
        }
        return var.getValue(profile);
    }

    /**
     * Resolves the variable for specified player from string format.
     *
     * @param variable the package with the variable, in {@code <package>:<variable>} format
     * @param profile  the {@link Profile} of the player
     * @return the value of parsed variable for given player
     * @throws QuestException if the package cannot be parsed, is not present or the variable could not be created
     * @see #getValue(QuestPackage, String, Profile)
     */
    public String getValue(final String variable, @Nullable final Profile profile) throws QuestException {
        final int index = variable.indexOf(':');
        if (index == -1) {
            throw new QuestException("Variable without explicit package '" + variable + "'! Expected format '<package>:<variable>'");
        }
        final String packString = variable.substring(0, index);
        final QuestPackage pack = Config.getPackages().get(packString);
        if (pack == null) {
            throw new QuestException("The variable '" + variable + "' reference the non-existent package '" + packString + "' !");
        }
        final String value = variable.substring(index + 1);
        return getValue(pack, '%' + value + '%', profile);
    }
}
