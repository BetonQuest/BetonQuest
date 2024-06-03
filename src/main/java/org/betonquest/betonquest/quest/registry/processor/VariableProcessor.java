package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.VariableID;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores Variables and resolve them.
 */
public class VariableProcessor extends QuestProcessor<Variable, VariableID> implements MetricSupplying {
    /**
     * Available Variable types.
     */
    private final Map<String, Class<? extends Variable>> variableTypes;

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
        super(log);
        this.variableTypes = variableTypes;
        this.loggerFactory = loggerFactory;
    }

    /**
     * Resolves variables in the supplied text and returns them as a list of
     * instruction strings, including % characters. Variables are unique, so if
     * the user uses the same variables multiple times, the list will contain
     * only one occurrence of this variable.
     *
     * @param text text from which the variables will be resolved
     * @return the list of unique variable instructions
     */
    public static List<String> resolveVariables(final String text) {
        final List<String> variables = new ArrayList<>();
        final Matcher matcher = Pattern.compile("%[^ %\\s]+%").matcher(text);
        while (matcher.find()) {
            final String variable = matcher.group();
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
        return variables;
    }

    @Override
    public Map.Entry<String, CompositeInstructionMetricsSupplier<?>> metricsSupplier() {
        return Map.entry("variables", new CompositeInstructionMetricsSupplier<>(values::keySet, variableTypes::keySet));
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
    public Variable createVariable(@Nullable final QuestPackage pack, final String instruction)
            throws InstructionParseException {
        final VariableID variableID;
        try {
            variableID = new VariableID(loggerFactory, pack, instruction);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Could not load variable: " + e.getMessage(), e);
        }
        // no need to create duplicated variables
        final Variable existingVariable = values.get(variableID);
        if (existingVariable != null) {
            return existingVariable;
        }
        final Instruction instructionVar = variableID.generateInstruction();
        final Class<? extends Variable> variableClass = variableTypes.get(instructionVar.current());
        // if it's null then there is no such type registered, log an error
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
    public String getVariableValue(final String packName, final String name, @Nullable final Profile profile) {
        if (!Config.getPackages().containsKey(packName)) {
            log.warn("Variable '" + name + "' contains the non-existent package '" + packName + "' !");
            return "";
        }
        final QuestPackage pack = Config.getPackages().get(packName);
        try {
            final Variable var = createVariable(pack, name);
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
