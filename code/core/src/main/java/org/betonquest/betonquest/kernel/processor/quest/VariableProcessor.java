package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.variable.VariableID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.betonquest.betonquest.kernel.registry.quest.VariableTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * Stores Variables and resolve them.
 */
public class VariableProcessor extends TypedQuestProcessor<VariableID, VariableAdapter> implements Variables {

    /**
     * Empty variables to satisfy object structure.
     */
    public static final Variables EMPTY_VARIABLES = new Variables() {
        @Override
        public VariableAdapter create(@Nullable final QuestPackage pack, final String instruction) throws QuestException {
            throw new QuestException("Not valid");
        }

        @Override
        public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws QuestException {
            throw new QuestException("Not valid");
        }

        @Override
        public String getValue(final String variable, @Nullable final Profile profile) throws QuestException {
            throw new QuestException("Not valid");
        }
    };

    /**
     * The Bukkit scheduler to run sync tasks.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Create a new Variable Processor to store variables, resolves them and create new.
     *
     * @param log           the custom logger for this class
     * @param packManager   the quest package manager to get quest packages from
     * @param variableTypes the available variable types
     * @param scheduler     the bukkit scheduler to run sync tasks
     * @param plugin        the plugin instance
     */
    public VariableProcessor(final BetonQuestLogger log, final QuestPackageManager packManager,
                             final VariableTypeRegistry variableTypes, final BukkitScheduler scheduler,
                             final Plugin plugin) {
        super(log, EMPTY_VARIABLES, packManager, variableTypes, "Variable", "variables");
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public void load(final QuestPackage pack) {
        // Empty
    }

    @Override
    protected VariableID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new VariableID(this, packManager, pack, identifier);
    }

    @Override
    public VariableAdapter create(@Nullable final QuestPackage pack, final String instruction)
            throws QuestException {
        final VariableID variableID;
        try {
            variableID = new VariableID(this, packManager, pack, instruction);
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
        final VariableAdapter variable;
        try {
            variable = create(pack, name);
        } catch (final QuestException e) {
            throw new QuestException("Could not create variable '" + name + "': " + e.getMessage(), e);
        }
        if (variable.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            return valueSync(profile, name, variable);
        }
        return value(profile, name, variable);
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

    private String valueSync(@Nullable final Profile profile, final String variableString, final VariableAdapter variable) throws QuestException {
        try {
            return scheduler.callSyncMethod(plugin, () -> value(profile, variableString, variable)).get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            throw new QuestException(e);
        }
    }

    private String value(@Nullable final Profile profile, final String variableString, final VariableAdapter variable) throws QuestException {
        try {
            return variable.getValue(profile);
        } catch (final QuestException e) {
            throw new QuestException("Error while resolving '" + variableString + "' variable: " + e.getMessage(), e);
        }
    }
}
