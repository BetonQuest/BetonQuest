package org.betonquest.betonquest.modules.logger.custom.chat;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This is a filter for {@link org.betonquest.betonquest.api.config.QuestPackage}.
 * A `*` at the end means any {@link org.betonquest.betonquest.api.config.QuestPackage}, that starts with the part before.
 * Only `*` means all {@link org.betonquest.betonquest.api.config.QuestPackage}s.
 */
public class PlayerPackageFilter implements PlayerFilter {

    /**
     * All active log filters for the in-game log.
     */
    private final Map<UUID, Map<String, Level>> playerFilters;

    /**
     * Create a new PlayerLogPackageFilter instance.
     */
    public PlayerPackageFilter() {
        this.playerFilters = new HashMap<>();
    }

    @Override
    public boolean addFilter(final UUID uuid, final String filter, final Level level) {
        if (!playerFilters.containsKey(uuid)) {
            playerFilters.put(uuid, new HashMap<>());
        }
        final Map<String, Level> filters = playerFilters.get(uuid);
        if (filters.containsKey(filter) && filters.get(filter).equals(level)) {
            return false;
        }
        filters.put(filter, level);
        return true;
    }

    @Override
    public boolean removeFilter(final UUID uuid, final String filter) {
        if (playerFilters.containsKey(uuid)) {
            final boolean removed = playerFilters.get(uuid).remove(filter) != null;
            if (playerFilters.get(uuid).isEmpty()) {
                playerFilters.remove(uuid);
            }
            return removed;
        }
        return false;
    }

    @Override
    public List<String> getFilters(final UUID uuid) {
        if (playerFilters.containsKey(uuid)) {
            return new ArrayList<>(playerFilters.get(uuid).keySet());
        }
        return new ArrayList<>();
    }

    @Override
    public Set<UUID> getUUIDs() {
        return playerFilters.keySet();
    }

    @Override
    public boolean filter(final UUID uuid, final String pack, final Level level) {
        final Map<String, Level> filterEntries = playerFilters.get(uuid);
        if (filterEntries != null) {
            for (final Map.Entry<String, Level> entry : filterEntries.entrySet()) {
                if (level.intValue() >= entry.getValue().intValue() && validPackage(pack, entry.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validPackage(final String pack, final String filter) {
        final boolean equal = !filter.endsWith("*");
        final String expression = equal ? filter : StringUtils.chop(filter);
        return equal && pack.equals(expression) || !equal && pack.startsWith(expression);
    }
}
