package org.betonquest.betonquest.database.holders;

import org.betonquest.betonquest.api.data.PointHolder;
import org.betonquest.betonquest.database.PlayerData;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of {@link PointHolder} for {@link PlayerData}.
 */
public class PlayerDataPointHolder implements PointHolder {

    /**
     * The player data access points from.
     */
    private final PlayerData playerData;

    /**
     * Creates a new instance of ProfilePointHolder.
     *
     * @param playerData the player data
     */
    public PlayerDataPointHolder(final PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public Map<String, Integer> get() {
        return playerData.getPoints().stream()
                .map(point -> Map.entry(point.getCategory(), point.getCount()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean has(final String category) {
        return get(category).isPresent();
    }

    @Override
    public Optional<Integer> get(final String category) {
        return playerData.getPointsFromCategory(category);
    }

    @Override
    public void set(final String category, final int points) {
        playerData.setPoints(category, points);
    }

    @Override
    public void add(final String category, final int points) {
        playerData.modifyPoints(category, points);
    }

    @Override
    public void remove(final String category) {
        playerData.removePointsCategory(category);
    }
}
