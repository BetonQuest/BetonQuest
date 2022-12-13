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
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public @Nullable
    String getReplacement(@Nullable final String arguments) {
        try {
            if (arguments == null) {
                return "";
            }
            final String[] args = arguments.split(":", 2);
            return BetonQuest.getInstance().getVariableValue(args[0], "%" + args[1] + "%", null);
        } catch (final Exception e) {
            LOG.warn("Could not parse hologram variable " + arguments + "! " +
                    "Expected format %<package>.<variable>%");
        }
        return arguments;
    }
}
