package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.GlobalPlaceholder;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays global placeholder <code>{bqg:package:variable}</code>.
 */
public class HologramGlobalPlaceholder implements GlobalPlaceholder {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(HologramGlobalPlaceholder.class);

    /**
     * Creates new instance of HologramGlobalPlaceholder
     */
    public HologramGlobalPlaceholder() {
    }

    @Override
    public int getRefreshIntervalTicks() {
        return 10 * 20;
    }

    @Override
    public @Nullable
    String getReplacement(@Nullable final String arguments) {
        if (arguments == null) {
            return "";
        }
        final int limit = 2;
        final String[] args = arguments.split(":", limit);
        if (args.length == limit) {
            return BetonQuest.getInstance().getVariableValue(args[0], "%" + args[1] + "%", null);
        }
        LOG.warn("Could not parse hologram variable " + arguments + "! " + "Expected format %<package>.<variable>%");
        return arguments;
    }
}
