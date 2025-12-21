package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.GlobalPlaceholder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays global placeholder <code>{bqg:package:placeholder}</code>.
 */
public class HologramGlobalPlaceholder implements GlobalPlaceholder {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Creates new instance of HologramGlobalPlaceholder.
     *
     * @param log          the logger that will be used for logging
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     */
    public HologramGlobalPlaceholder(final BetonQuestLogger log, final Placeholders placeholders) {
        this.log = log;
        this.placeholders = placeholders;
    }

    @Override
    public int getRefreshIntervalTicks() {
        return 10 * 20;
    }

    @Override
    @Nullable
    public String getReplacement(@Nullable final String arguments) {
        if (arguments == null) {
            return "";
        }
        try {
            return placeholders.getValue(arguments, null);
        } catch (final QuestException e) {
            log.warn("Could not parse hologram placeholder '" + arguments + "': " + e.getMessage(), e);
            return arguments;
        }
    }
}
