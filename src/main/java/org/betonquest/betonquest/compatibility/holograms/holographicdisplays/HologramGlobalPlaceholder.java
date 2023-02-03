package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import lombok.CustomLog;
import me.filoghost.holographicdisplays.api.placeholder.GlobalPlaceholder;
import org.betonquest.betonquest.BetonQuest;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays global placeholder <code>{bqg:package:variable}</code>.
 */
@CustomLog
public class HologramGlobalPlaceholder implements GlobalPlaceholder {

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
