package org.betonquest.betonquest.kernel.processor;

import dev.faststats.data.Metric;
import dev.faststats.data.SourceId;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.bstats.MetricsHolder;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;
import org.betonquest.betonquest.util.MetricsUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Does the logic around a quest type and stores their type registry.
 * Also provides their BStats metrics.
 *
 * @param <I> the {@link ReadableIdentifier} identifying the type
 * @param <T> the legacy type
 */
public abstract class TypedQuestProcessor<I extends ReadableIdentifier, T> extends QuestProcessor<I, T> implements MetricsHolder {

    /**
     * Available types.
     */
    protected final FactoryTypeRegistry<T> types;

    /**
     * Instruction API.
     */
    protected final Instructions instructionApi;

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
                               final Instructions instructionApi, final String readable, @SourceId final String internal) {
        super(log, identifierFactory, readable, internal);
        this.types = types;
        this.instructionApi = instructionApi;
    }

    @Override
    public Map.Entry<String, CompositeInstructionMetricsSupplier<?>> metricsSupplier() {
        return Map.entry(internal, new CompositeInstructionMetricsSupplier<>(values::keySet, types::keySet));
    }

    @Override
    public Set<Metric<?>> getMetrics() {
        final Set<Metric<?>> metrics = new HashSet<>(super.getMetrics());
        metrics.add(Metric.numberMap(internal + "_type_count", () -> MetricsUtils.typeCountMetrics(values.keySet(), types.keySet(), instructionApi)));
        return metrics;
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(internal);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(true)) {
            if (key.contains(" ")) {
                log.warn(pack, readable + " name cannot contain spaces: '" + key + "' in pack '" + pack.getQuestPath() + "'");
                continue;
            }
            try {
                if (section.isConfigurationSection(key)) {
                    loadSectionKey(key, pack);
                } else {
                    loadKey(key, pack);
                }
            } catch (final QuestException e) {
                log.warn(pack, "Error while loading " + readable + " '" + key + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    private void loadKey(final String key, final QuestPackage pack) throws QuestException {
        final I identifier = getIdentifier(pack, key);
        final Instruction instruction = instructionApi.create(identifier, identifier.readRawInstruction());
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

    private void loadSectionKey(final String key, final QuestPackage pack) throws QuestException {
        final SectionFactory<I, T> sectionFactory = getSectionFactory();
        if (sectionFactory == null) {
            return;
        }
        final I identifier = getIdentifier(pack, key);
        try {
            final T parsed = sectionFactory.fromSection(identifier);
            values.put(identifier, parsed);
            postCreation(identifier, parsed);
            log.debug(pack, "  " + readable + " '" + identifier + "' loaded");
        } catch (final QuestException e) {
            throw new QuestException("Error in '%s' %s (implicit section): %s".formatted(identifier, readable, e.getMessage()), e);
        }
    }

    /**
     * Get the factory for creating {@link T} for sections.
     * <p>
     * Only implement that method if the {@link T} should have default implementations
     * for {@link ConfigurationSection} nodes.
     *
     * @return the factory, if section nodes should get implementations
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Nullable
    protected SectionFactory<I, T> getSectionFactory() {
        return null;
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

    /**
     * Factory to create implementation that works on sections instead instruction strings.
     *
     * @param <I> the {@link ReadableIdentifier} identifying the type
     * @param <T> the type
     */
    @FunctionalInterface
    protected interface SectionFactory<I, T> {

        /**
         * Create a new {@link T} from an {@link Instruction}.
         *
         * @param sectionIdentifier the section to create for
         * @return the newly created {@link T}
         * @throws QuestException if the section cannot be parsed
         */
        @Contract(pure = true)
        T fromSection(I sectionIdentifier) throws QuestException;
    }
}
