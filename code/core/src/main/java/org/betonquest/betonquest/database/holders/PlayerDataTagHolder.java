package org.betonquest.betonquest.database.holders;

import org.betonquest.betonquest.api.data.TagHolder;
import org.betonquest.betonquest.database.PlayerData;

import java.util.Set;

/**
 * An implementation of {@link TagHolder} for {@link PlayerData}.
 */
public class PlayerDataTagHolder implements TagHolder {

    /**
     * The player data access tags from.
     */
    private final PlayerData playerData;

    /**
     * Creates a new instance of PlayerDataTagHolder.
     *
     * @param playerData the player data
     */
    public PlayerDataTagHolder(final PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public Set<String> get() {
        return playerData.getTags();
    }

    @Override
    public boolean has(final String tag) {
        return playerData.hasTag(tag);
    }

    @Override
    public void add(final String tag) {
        playerData.addTag(tag);
    }

    @Override
    public void remove(final String tag) {
        playerData.removeTag(tag);
    }
}
