package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Does the logic around a quest type and stores their type registry.
 * Also provides their BStats metrics.
 *
 * @param <I> the {@link InstructionIdentifier} identifying the type
 * @param <T> the legacy type
 */
public abstract class TypedQuestProcessor<I extends InstructionIdentifier, T> extends QuestProcessor<I, T> {

    /**
     * Available types.
     */
    protected final FactoryTypeRegistry<T> types;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log         the custom logger for this class
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param types       the available types
     * @param readable    the type name used for logging, with the first letter in upper case
     * @param internal    the section name and/or bstats topic identifier
     */
    public TypedQuestProcessor(final BetonQuestLogger log, final Variables variables, final QuestPackageManager packManager,
                               final FactoryTypeRegistry<T> types, final String readable, final String internal) {
        super(log, variables, packManager, readable, internal);
        this.types = types;
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
        final String type = identifier.getInstruction().getPart(0);
        final TypeFactory<T> factory = types.getFactory(type);
        try {
            final T parsed = factory.parseInstruction(identifier.getInstruction());
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
