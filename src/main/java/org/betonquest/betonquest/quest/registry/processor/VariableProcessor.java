package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.VariableID;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Stores Variables and resolve them.
 */
public class VariableProcessor extends TypedQuestProcessor<VariableID, Variable, Class<? extends Variable>> {
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
    public VariableProcessor(final BetonQuestLogger log, final Map<String, Class<? extends Variable>> variableTypes,
                             final BetonQuestLoggerFactory loggerFactory) {
        super(log, variableTypes, "variables");
        this.loggerFactory = loggerFactory;
    }

    @Override
    public void load(final QuestPackage pack) {
        // Empty
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
    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Nullable
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
        final Class<? extends Variable> variableClass = types.get(instructionVar.current());
        if (variableClass == null) {
            throw new InstructionParseException("Variable type " + instructionVar.current() + " is not registered");
        }

        try {
            final Variable variable = variableClass.getConstructor(Instruction.class).newInstance(instructionVar);
            values.put(variableID, variable);
            log.debug(pack, "Variable " + variableID + " loaded");
            return variable;
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in " + variableID + " variable: " + e.getCause().getMessage(), e);
            } else {
                log.reportException(pack, e);
            }
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            log.reportException(pack, e);
        }
        return null;
    }

    /**
     * Resoles the variable for specified player. If the variable is not loaded
     * it will load it on the main thread.
     *
     * @param packName name of the package
     * @param name     name of the variable (instruction, with % characters)
     * @param profile  the {@link Profile} of the player
     * @return the value of this variable for given player
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public String getValue(final String packName, final String name, @Nullable final Profile profile) {
        if (!Config.getPackages().containsKey(packName)) {
            log.warn("Variable '" + name + "' contains the non-existent package '" + packName + "' !");
            return "";
        }
        final QuestPackage pack = Config.getPackages().get(packName);
        try {
            final Variable var = create(pack, name);
            if (var == null) {
                log.warn(pack, "Could not resolve variable '" + name + "'.");
                return "";
            }
            if (profile == null && !var.isStaticness()) {
                log.warn(pack, "Variable '" + name + "' cannot be executed without a profile reference!");
                return "";
            }
            return var.getValue(profile);
        } catch (final InstructionParseException e) {
            log.warn(pack, "&cCould not create variable '" + name + "': " + e.getMessage(), e);
            return "";
        }
    }
}
