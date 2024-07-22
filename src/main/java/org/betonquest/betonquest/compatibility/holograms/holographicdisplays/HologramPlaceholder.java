package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.IndividualPlaceholder;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays placeholder <code>{bq:package:variable}</code>.
 */
public class HologramPlaceholder implements IndividualPlaceholder {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates new instance of HologramPlaceholder
     *
     * @param log the logger that will be used for logging
     */
    public HologramPlaceholder(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public int getRefreshIntervalTicks() {
        return 10 * 20;
    }

    @Override
    @Nullable
    public String getReplacement(final Player player, @Nullable final String arguments) {
        if (arguments == null) {
            return "";
        }
        final Profile profile = PlayerConverter.getID(player);
        final int limit = 2;
        final String[] args = arguments.split(":", limit);
        if (args.length == limit) {
            return BetonQuest.getInstance().getVariableValue(args[0], "%" + args[1] + "%", profile);
        }
        log.warn("Could not parse hologram variable " + arguments + "! Expected format %<package>.<variable>%");
        return arguments;
    }
}
