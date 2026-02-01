package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Does the logic around a quest type and stores their type registry.
 * Also provides their BStats metrics.
 *
 * @param <I> the {@link ReadableIdentifier} identifying the type
 * @param <T> the legacy type
 */
public abstract class TypedQuestProcessor<I extends ReadableIdentifier, T> extends QuestProcessor<I, T> {

    /**
     * Available types.
     */
    protected final FactoryTypeRegistry<T> types;

    /**
     * Instruction API.
     */
    protected final InstructionApi instructionApi;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log               the custom logger for this class
     * @param types             the available types
     * @param identifierFactory the identifier factory to create {@link ReadableIdentifier}s for this type
     * @param instructionApi    the instruction api
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     */
    public TypedQuestProcessor(final BetonQuestLogger log,
                               final FactoryTypeRegistry<T> types, final IdentifierFactory<I> identifierFactory,
                               final InstructionApi instructionApi, final String readable, final String internal) {
        super(log, identifierFactory, readable, internal);
        this.types = types;
        this.instructionApi = instructionApi;
    }

    /**
     * Gets the bstats metric supplier for registered and active types.
     *
     * @return the metric with its type identifier
     */
    public Map.Entry<String, CompositeInstructionMetricsSupplier<?>> metricsSupplier() {
        return Map.entry(internal, new CompositeInstructionMetricsSupplier<>(values::keySet, types::keySet));
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(internal);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            if (key.contains(" ")) {
                log.warn(pack, readable + " name cannot contain spaces: '" + key + "' in pack '" + pack.getQuestPath() + "'");
                continue;
            }
            try {
                loadKey(key, pack);
            } catch (final QuestException e) {
                log.warn(pack, "Error while loading " + readable + " '" + key + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    private void loadKey(final String key, final QuestPackage pack) throws QuestException {
        final I identifier = getIdentifier(pack, key);
        final Instruction instruction = instructionApi.createInstruction(identifier, identifier.readRawInstruction());
        final String type = instruction.getPart(0);
        try {
            final TypeFactory<T> factory = types.getFactory(type);
            final T parsed = factory.parseInstruction(instruction);
            values.put(identifier, parsed);
            postCreation(identifier, parsed);
            log.debug(pack, "  " + readable + " '" + identifier + "' loaded");
        } catch (final QuestException e) {
            throw new QuestException("Error in '" + identifier + "' " + readable + " (" + type + "): " + e.getMessage(), e);
        }
    }

    /**
     * Allows for using the {@link T} after successful creation.
     *
     * @param identifier the id of the created {@link T}
     * @param value      the newly created {@link T}
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void postCreation(final I identifier, final T value) {
        // Empty
    }
}
