package org.betonquest.betonquest.database.holders;

import org.betonquest.betonquest.api.data.PointHolder;
import org.betonquest.betonquest.database.GlobalData;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of {@link PointHolder} for {@link GlobalData}.
 */
public class GlobalDataPointHolder implements PointHolder {

    /**
     * The global data access points from.
     */
    private final GlobalData globalData;

    /**
     * Creates a new instance of GlobalDataPointHolder.
     *
     * @param globalData the global data
     */
    public GlobalDataPointHolder(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public Map<String, Integer> get() {
        return globalData.getPoints().stream()
                .map(point -> Map.entry(point.getCategory(), point.getCount()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean has(final String category) {
        return get(category).isPresent();
    }

    @Override
    public Optional<Integer> get(final String category) {
        return globalData.getPointsFromCategory(category);
    }

    @Override
    public void set(final String category, final int points) {
        globalData.setPoints(category, points);
    }

    @Override
    public void add(final String category, final int points) {
        globalData.modifyPoints(category, points);
    }

    @Override
    public void remove(final String category) {
        globalData.removePointsCategory(category);
    }
}
