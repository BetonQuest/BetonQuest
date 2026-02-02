package org.betonquest.betonquest.kernel.registry;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the Factories for Quest Types that can be used in BetonQuest.
 *
 * @param <P> the player variant of the type
 * @param <S> the playerless variant of the type
 * @param <A> the adapter structure to store {@link P} and {@link S} in one object
 */
public abstract class QuestTypeRegistry<P, S, A> extends FactoryTypeRegistry<A> implements CoreQuestRegistry<P, S> {

    /**
     * Create a new type registry.
     *
     * @param log      the logger that will be used for logging
     * @param typeName the name of the type to use in the register log message
     */
    public QuestTypeRegistry(final BetonQuestLogger log, final String typeName) {
        super(log, typeName);
    }

    @Override
    public void register(final String name, final PlayerQuestFactory<P> factory) {
        registerInternal(name, factory, null);
    }

    @Override
    public void register(final String name, final PlayerlessQuestFactory<S> playerlessFactory) {
        registerInternal(name, null, playerlessFactory);
    }

    @Override
    public <C extends PlayerQuestFactory<P> & PlayerlessQuestFactory<S>> void registerCombined(final String name, final C factory) {
        register(name, factory, factory);
    }

    @Override
    public void register(final String name, final PlayerQuestFactory<P> playerFactory, final PlayerlessQuestFactory<S> playerlessFactory) {
        registerInternal(name, playerFactory, playerlessFactory);
    }

    /**
     * Either the player factory or the playerless factory has to be present.
     *
     * @see #getFactoryAdapter(PlayerQuestFactory, PlayerlessQuestFactory)
     */
    private void registerInternal(final String name, @Nullable final PlayerQuestFactory<P> playerFactory,
                                  @Nullable final PlayerlessQuestFactory<S> playerlessFactory) {
        log.debug("Registering " + name + " " + typeName + " type");
        types.put(name, getFactoryAdapter(playerFactory, playerlessFactory));
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
    protected abstract TypeFactory<A> getFactoryAdapter(
            @Nullable PlayerQuestFactory<P> playerFactory,
            @Nullable PlayerlessQuestFactory<S> playerlessFactory);
}
