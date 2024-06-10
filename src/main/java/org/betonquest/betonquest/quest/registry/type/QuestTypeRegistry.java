package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.quest.ComposedQuestTypeAdapter;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores the {@link T Types} that can be used in BetonQuest.
 *
 * @param <T> the stored type as defined in {@link PlayerQuestFactory}
 * @param <S> the playerless variant of {@link T}
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
     * @deprecated replaced by {@link #register(String, PlayerQuestFactory, PlayerlessQuestFactory)}
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
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the {@link T}
     */
    public void register(final String name, final PlayerQuestFactory<T> factory) {
        registerInternal(name, factory, null);
    }

    /**
     * Registers a type and a factory to create new playerless instances.
     *
     * @param name              the name of the type
     * @param playerlessFactory the playerless factory to create the {@link S}
     */
    public void register(final String name, final PlayerlessQuestFactory<S> playerlessFactory) {
        registerInternal(name, null, playerlessFactory);
    }

    /**
     * Registers a type with its name and a composed factory to create player and playerless instances.
     *
     * @param name            the name of the {@link T}
     * @param composedFactory the factory to create the player and playerless type instances
     */
    public void register(final String name, final ComposedQuestFactory<C> composedFactory) {
        final ComposedQuestTypeAdapter<C, T, S> composedAdapter = getComposedAdapter(composedFactory);
        register(name, composedAdapter, composedAdapter);
    }

    /**
     * Get a new adapter of {@link PlayerQuestFactory} and {@link PlayerlessQuestFactory}s from the {@link ComposedQuestFactory}.
     *
     * @param composedFactory the composed factory to adapt
     * @return the adapter to store
     */
    protected abstract ComposedQuestTypeAdapter<C, T, S> getComposedAdapter(ComposedQuestFactory<C> composedFactory);

    /**
     * Registers a type with its name and a single factory to create both player and playerless instances.
     *
     * @param name    name of the type
     * @param factory factory to create the player and playerless variant
     * @param <Q>     type of factory that creates both normal and playerless instances of the type
     */
    public <Q extends PlayerQuestFactory<T> & PlayerlessQuestFactory<S>> void registerCombined(final String name, final Q factory) {
        register(name, factory, factory);
    }

    /**
     * Registers a type with its name and two factories to create player and playerless instances.
     *
     * @param name              name of the type
     * @param playerFactory     factory to create the {@link T}
     * @param playerlessFactory factory to create the {@link S}
     */
    public void register(final String name, final PlayerQuestFactory<T> playerFactory, final PlayerlessQuestFactory<S> playerlessFactory) {
        registerInternal(name, playerFactory, playerlessFactory);
    }

    /**
     * Either the player factory or the playerless factory has to be present.
     *
     * @see #getLegacyFactoryAdapter(PlayerQuestFactory, PlayerlessQuestFactory)
     */
    private void registerInternal(final String name, @Nullable final PlayerQuestFactory<T> playerFactory,
                                  @Nullable final PlayerlessQuestFactory<S> playerlessFactory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, getLegacyFactoryAdapter(playerFactory, playerlessFactory));
    }

    /**
     * Get a new adapter to the legacy factory from the new type format.
     * <p>
     * Either the player factory or the playerless factory has to be present.
     *
     * @param playerFactory     player factory to create the {@link T}
     * @param playerlessFactory playerless factory to create the {@link S}
     * @return the legacy factory to store
     */
    protected abstract LegacyTypeFactory<L> getLegacyFactoryAdapter(
            @Nullable PlayerQuestFactory<T> playerFactory,
            @Nullable PlayerlessQuestFactory<S> playerlessFactory);

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
