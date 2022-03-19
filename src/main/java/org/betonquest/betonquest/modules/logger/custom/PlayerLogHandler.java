package org.betonquest.betonquest.modules.logger.custom;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public class PlayerLogHandler extends Handler {

    /**
     * The {@link BukkitAudiences} instance responsible for sending messages.
     */
    private final BukkitAudiences bukkitAudiences;

    /**
     * All active log filters for the in-game log.
     */
    private final Map<UUID, Map<String, Level>> playerFilters;

    /**
     * Creates a new {@link PlayerLogHandler}.
     *
     * @param bukkitAudiences The {@link BukkitAudiences} instance for sending messages.
     */
    public PlayerLogHandler(final BukkitAudiences bukkitAudiences) {
        super();
        this.bukkitAudiences = bukkitAudiences;
        this.playerFilters = new HashMap<>();
    }

    /**
     * Logs a LogRecord to the history or the target handler.
     * <br><br>
     * The {@link java.util.logging.MemoryHandler#publish} method was used as reference.
     *
     * @param record The LogRecord to log
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void publish(final LogRecord record) {
        if (!(record instanceof BetonQuestLogRecord) || playerFilters.isEmpty() || !isLoggable(record)) {
            return;
        }
        final String msg;
        try {
            msg = getFormatter().format(record);
        } catch (final Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        final String pack = ((BetonQuestLogRecord) record).getPack();
        filterPlayers(record, msg, pack);
    }

    private void filterPlayers(final LogRecord record, final String msg, final String pack) {
        players:
        for (final Map.Entry<UUID, Map<String, Level>> filterEntries : playerFilters.entrySet()) {
            for (final Map.Entry<String, Level> entry : filterEntries.getValue().entrySet()) {
                if (record.getLevel().intValue() < entry.getValue().intValue()) {
                    continue;
                }
                if (validPackage(pack, entry.getKey())) {
                    bukkitAudiences.player(filterEntries.getKey()).sendMessage(GsonComponentSerializer.gson().deserialize(msg));
                    continue players;
                }
            }
        }
    }

    private boolean validPackage(final String pack, final String filter) {
        final boolean equal = !filter.endsWith("*");
        final String expression = equal ? filter : StringUtils.chop(filter);
        return equal && pack.equals(expression) || !equal && pack.startsWith(expression);
    }

    @Override
    public void flush() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }

    /**
     * Adds a filter to a player.
     *
     * @param uuid   The {@link UUID} of the player
     * @param filter The filter pattern
     * @param level  The {@link Level} of the filter
     * @return True if the filter was successfully added
     */
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

    /**
     * Removes a filter from a player.
     *
     * @param uuid   The {@link UUID} of the player
     * @param filter The filter pattern
     * @return True if the filter was successfully removed
     */
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

    /**
     * Gets a players filters.
     *
     * @param uuid The {@link UUID} of the player
     * @return A list of filters
     */
    public List<String> getFilters(final UUID uuid) {
        if (playerFilters.containsKey(uuid)) {
            return new ArrayList<>(playerFilters.get(uuid).keySet());
        }
        return new ArrayList<>();
    }
}
