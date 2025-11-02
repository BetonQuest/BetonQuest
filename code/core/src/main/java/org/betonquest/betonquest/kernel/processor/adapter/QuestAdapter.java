package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to store the player and playerless variant.
 *
 * @param <P> the player variant
 * @param <L> the playerless variant
 */
public class QuestAdapter<P, L> {
    /**
     * Package where the types are from.
     */
    @Nullable
    protected final QuestPackage pack;

    /**
     * Player variant.
     */
    @Nullable
    protected final P player;

    /**
     * Playerless variant.
     */
    @Nullable
    protected final L playerless;

    /**
     * Create a new Adapter with instruction and at least one type.
     *
     * @param pack       the package where the types are from
     * @param player     the type requiring a profile for execution
     * @param playerless the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     */
    public QuestAdapter(@Nullable final QuestPackage pack, @Nullable final P player, @Nullable final L playerless) {
        if (player == null && playerless == null) {
            throw new IllegalStateException("Cannot create a Wrapper without a value!");
        }
        this.pack = pack;
        this.player = player;
        this.playerless = playerless;
    }

    /**
     * If it allows playerless it can be used with null profiles.
     *
     * @return if a playerless implementation is present
     */
    public boolean allowsPlayerless() {
        return playerless != null;
    }

    /**
     * Get the pack where the content of this adapter is from.
     *
     * @return the source pack
     */
    @Nullable
    public QuestPackage getPackage() {
        return pack;
    }
}
