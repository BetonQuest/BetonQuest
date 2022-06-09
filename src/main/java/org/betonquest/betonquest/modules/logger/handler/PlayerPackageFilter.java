package org.betonquest.betonquest.modules.logger.handler;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
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
    public boolean addFilter(final UUID uuid, final String pattern, final Level level) {
        if (!playerFilters.containsKey(uuid)) {
            playerFilters.put(uuid, new HashMap<>());
        }
        final Map<String, Level> filters = playerFilters.get(uuid);
        if (filters.containsKey(pattern) && filters.get(pattern).equals(level)) {
            return false;
        }
        filters.put(pattern, level);
        return true;
    }

    @Override
    public boolean removeFilter(final UUID uuid, final String pattern) {
        if (playerFilters.containsKey(uuid)) {
            final boolean removed = playerFilters.get(uuid).remove(pattern) != null;
            if (playerFilters.get(uuid).isEmpty()) {
                playerFilters.remove(uuid);
            }
            return removed;
        }
        return false;
    }

    @Override
    public Set<String> getFilters(final UUID uuid) {
        if (playerFilters.containsKey(uuid)) {
            return playerFilters.get(uuid).keySet();
        }
        return Set.of();
    }

    @Override
    public Set<UUID> getUUIDs() {
        return playerFilters.keySet();
    }

    @Override
    public boolean match(final UUID uuid, final String pack, final Level level) {
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

    private boolean validPackage(final String pack, final String pattern) {
        final boolean equal = !pattern.endsWith("*");
        final String expression = equal ? pattern : StringUtils.chop(pattern);
        return equal && pack.equals(expression) || !equal && pack.startsWith(expression);
    }
}
