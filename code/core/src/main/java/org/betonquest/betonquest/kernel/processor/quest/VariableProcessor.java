package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.variable.VariableID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.betonquest.betonquest.kernel.registry.quest.VariableTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores Variables and resolve them.
 */
public class VariableProcessor extends TypedQuestProcessor<VariableID, VariableAdapter> implements Variables {

    /**
     * Create a new Variable Processor to store variables, resolves them and create new.
     *
     * @param log           the custom logger for this class
     * @param packManager   the quest package manager to get quest packages from
     * @param variableTypes the available variable types
     */
    public VariableProcessor(final BetonQuestLogger log, final QuestPackageManager packManager,
                             final VariableTypeRegistry variableTypes) {
        super(log, packManager, variableTypes, "Variable", "variables");
    }

    @Override
    public void load(final QuestPackage pack) {
        // Empty
    }

    @Override
    protected VariableID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new VariableID(packManager, pack, identifier);
    }

    @Override
    public VariableAdapter create(@Nullable final QuestPackage pack, final String instruction)
            throws QuestException {
        final VariableID variableID;
        try {
            variableID = new VariableID(packManager, pack, instruction);
        } catch (final QuestException e) {
            throw new QuestException("Could not load variable: " + e.getMessage(), e);
        }
        final VariableAdapter existingVariable = values.get(variableID);
        if (existingVariable != null) {
            return existingVariable;
        }
        final Instruction instructionVar = variableID.getInstruction();
        final TypeFactory<VariableAdapter> variableFactory = types.getFactory(instructionVar.current());
        final VariableAdapter variable = variableFactory.parseInstruction(instructionVar);
        values.put(variableID, variable);
        log.debug(pack, "Variable " + variableID + " loaded");
        return variable;
    }

    @Override
    public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws QuestException {
        final VariableAdapter var;
        try {
            var = create(pack, name);
        } catch (final QuestException e) {
            throw new QuestException("Could not create variable '" + name + "': " + e.getMessage(), e);
        }
        return var.getValue(profile);
    }

    @Override
    public String getValue(final String variable, @Nullable final Profile profile) throws QuestException {
        final int index = variable.indexOf(':');
        if (index == -1) {
            throw new QuestException("Variable without explicit package '" + variable + "'! Expected format '<package>:<variable>'");
        }
        final String packString = variable.substring(0, index);
        final QuestPackage pack = packManager.getPackage(packString);
        if (pack == null) {
            throw new QuestException("The variable '" + variable + "' reference the non-existent package '" + packString + "' !");
        }
        final String value = variable.substring(index + 1);
        return getValue(pack, '%' + value + '%', profile);
    }
}
