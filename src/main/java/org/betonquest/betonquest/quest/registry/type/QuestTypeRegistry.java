package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.betonquest.betonquest.quest.registry.FactoryRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the Factories for Quest Types that can be used in BetonQuest.
 *
 * @param <P> the player variant of the type
 * @param <S> the playerless variant of the type
 * @param <L> the legacy structure based on the {@link org.betonquest.betonquest.instruction.Instruction Instruction}
 *            as defined in the {@link org.betonquest.betonquest.api API package}
 */
public abstract class QuestTypeRegistry<P, S, L> extends FactoryRegistry<TypeFactory<L>> {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     * @param typeName      the name of the type to use in the register log message
     */
    public QuestTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final String typeName) {
        super(log, typeName);
        this.loggerFactory = loggerFactory;
    }

    /**
     * Registers a type with its name and the class used to create instances of the type.
     *
     * @param name   the name of the type
     * @param lClass the class object for the type
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
     * @param lClass the class object for the type
     * @return the legacy factory to store
     * @deprecated the legacy system is subject to removal
     */
    @Deprecated
    protected abstract LegacyTypeFactory<L> getFromClassLegacyTypeFactory(BetonQuestLogger log, Class<? extends L> lClass);

    /**
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     */
    public void register(final String name, final PlayerQuestFactory<P> factory) {
        registerInternal(name, factory, null);
    }

    /**
     * Registers a type and a factory to create new playerless instances.
     *
     * @param name              the name of the type
     * @param playerlessFactory the playerless factory to create the type
     */
    public void register(final String name, final PlayerlessQuestFactory<S> playerlessFactory) {
        registerInternal(name, null, playerlessFactory);
    }

    /**
     * Registers a type with its name and a single factory to create both player and playerless instances.
     *
     * @param name    the name of the type
     * @param factory the factory to create the player and playerless variant
     * @param <C>     the type of factory that creates both normal and playerless instances of the type
     */
    public <C extends PlayerQuestFactory<P> & PlayerlessQuestFactory<S>> void registerCombined(final String name, final C factory) {
        register(name, factory, factory);
    }

    /**
     * Registers a type with its name and two factories to create player and playerless instances.
     *
     * @param name              the name of the type
     * @param playerFactory     the player factory to create the type
     * @param playerlessFactory the playerless factory to create the type
     */
    public void register(final String name, final PlayerQuestFactory<P> playerFactory, final PlayerlessQuestFactory<S> playerlessFactory) {
        registerInternal(name, playerFactory, playerlessFactory);
    }

    /**
     * Either the player factory or the playerless factory has to be present.
     *
     * @see #getLegacyFactoryAdapter(PlayerQuestFactory, PlayerlessQuestFactory)
     */
    private void registerInternal(final String name, @Nullable final PlayerQuestFactory<P> playerFactory,
                                  @Nullable final PlayerlessQuestFactory<S> playerlessFactory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, getLegacyFactoryAdapter(playerFactory, playerlessFactory));
    }

    /**
     * Get a new adapter to the legacy factory from the new type format.
     * <p>
     * Either the player factory or the playerless factory has to be present.
     *
     * @param playerFactory     the player factory to create the type
     * @param playerlessFactory the playerless factory to create the type
     * @return the legacy factory to store
     */
    protected abstract LegacyTypeFactory<L> getLegacyFactoryAdapter(
            @Nullable PlayerQuestFactory<P> playerFactory,
            @Nullable PlayerlessQuestFactory<S> playerlessFactory);
}
