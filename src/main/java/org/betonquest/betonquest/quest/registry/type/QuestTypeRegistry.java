package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;
import org.betonquest.betonquest.quest.ComposedQuestTypeAdapter;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores the {@link T Types} that can be used in BetonQuest.
 *
 * @param <T> the stored type as defined in {@link QuestFactory}
 * @param <S> the static variant of {@link T} - also named {@code static T}
 * @param <C> the composed type extending {@link T} and {@link S}
 * @param <L> the legacy structure based on the {@link org.betonquest.betonquest.Instruction Instruction}
 *            as defined in the {@link org.betonquest.betonquest.api API package}
 */
public abstract class QuestTypeRegistry<T, S, C, L> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Name of the type to display in register log.
     */
    private final String typeName;

    /**
     * Map of registered legacy factories.
     */
    private final Map<String, LegacyTypeFactory<L>> types = new HashMap<>();

    /**
     * Create a new type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     * @param typeName      the name of the type to use in the register log message
     */
    public QuestTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final String typeName) {
        this.log = log;
        this.loggerFactory = loggerFactory;
        this.typeName = typeName;
    }

    /**
     * Registers a type with its name and the class used to create instances of the type.
     *
     * @param name   name of the {@link T} type
     * @param lClass class object for the {@link T}
     * @deprecated replaced by {@link #register(String, QuestFactory, StaticQuestFactory)}
     */
    @Deprecated
    public void register(final String name, final Class<? extends L> lClass) {
        log.debug("Registering " + name + " [legacy]" + typeName + " type");
        types.put(name, getFromClassLegacyTypeFactory(loggerFactory.create(lClass), lClass));
    }

    /**
     * Create a new legacy type factory which will be used to create new instances of the {@link L}.
     *
     * @param log    the log to use in the factory
     * @param lClass the class object for the {@link T}
     * @return the legacy factory to store
     */
    protected abstract LegacyTypeFactory<L> getFromClassLegacyTypeFactory(BetonQuestLogger log, Class<? extends L> lClass);

    /**
     * Registers an {@link T} that does not support static execution with its name
     * and a factory to create new normal instances of the {@link T}.
     *
     * @param name    name of the {@link T}
     * @param factory factory to create the {@link T}
     */
    public void register(final String name, final QuestFactory<T> factory) {
        registerInternal(name, factory, null);
    }

    /**
     * Registers an {@link T} and a factory to create new static instances of the {@link T}.
     *
     * @param name          name of the {@link T}
     * @param staticFactory static factory to create the {@link T}
     */
    public void register(final String name, final StaticQuestFactory<S> staticFactory) {
        registerInternal(name, null, staticFactory);
    }

    /**
     * Registers a type with its name and a composed factory to create normal and
     * static instances of the type.
     *
     * @param name            name of the {@link T}
     * @param composedFactory factory to create the normal and static {@link T}
     */
    public void register(final String name, final ComposedQuestFactory<C> composedFactory) {
        final ComposedQuestTypeAdapter<C, T, S> composedAdapter = getComposedAdapter(composedFactory);
        register(name, composedAdapter, composedAdapter);
    }

    /**
     * Get a new adapter of {@link QuestFactory} and {@link StaticQuestFactory}s from the {@link ComposedQuestFactory}.
     *
     * @param composedFactory the composed factory to adapt
     * @return the adapter to store
     */
    protected abstract ComposedQuestTypeAdapter<C, T, S> getComposedAdapter(ComposedQuestFactory<C> composedFactory);

    /**
     * Registers a {@link T} with its name and a single factory to create both normal and
     * static instances of the {@link T}.
     *
     * @param name    name of the {@link T}
     * @param factory factory to create the {@link T} and the static variant
     * @param <Q>     type of factory that creates both normal and static instances of the {@link T}
     */
    public <Q extends QuestFactory<T> & StaticQuestFactory<S>> void registerCombined(final String name, final Q factory) {
        register(name, factory, factory);
    }

    /**
     * Registers a type with its name and two factories to create normal and
     * static instances of the type.
     *
     * @param name          name of the {@link T}
     * @param factory       factory to create the {@link T}
     * @param staticFactory factory to create the static {@link T}
     */
    public void register(final String name, final QuestFactory<T> factory, final StaticQuestFactory<S> staticFactory) {
        registerInternal(name, factory, staticFactory);
    }

    /**
     * Either the factory or the static factory has to be present.
     *
     * @see #getLegacyFactoryAdapter(QuestFactory, StaticQuestFactory)
     */
    private void registerInternal(final String name, @Nullable final QuestFactory<T> factory,
                                  @Nullable final StaticQuestFactory<S> staticFactory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, getLegacyFactoryAdapter(factory, staticFactory));
    }

    /**
     * Get a new adapter to the legacy factory from the new type format.
     * <p>
     * Either the factory or the static factory has to be present.
     *
     * @param factory       factory to create the {@link T}
     * @param staticFactory factory to create the static {@link T}
     * @return the legacy factory to store
     */
    protected abstract LegacyTypeFactory<L> getLegacyFactoryAdapter(@Nullable QuestFactory<T> factory, @Nullable StaticQuestFactory<S> staticFactory);

    /**
     * Fetches the factory to create the type registered with the given name.
     *
     * @param name the name of the type
     * @return a factory to create the type
     */
    @Nullable
    public LegacyTypeFactory<L> getFactory(final String name) {
        return types.get(name);
    }

    /**
     * Gets the keys of all registered types.
     *
     * @return the actual key set
     */
    public Set<String> keySet() {
        return types.keySet();
    }
}
