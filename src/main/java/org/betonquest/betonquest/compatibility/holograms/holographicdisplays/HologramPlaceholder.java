package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import lombok.CustomLog;
import me.filoghost.holographicdisplays.api.placeholder.IndividualPlaceholder;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays placeholder <code>{bq:package:variable}</code>.
 */
@CustomLog
public class HologramPlaceholder implements IndividualPlaceholder {

    /**
     * Creates new instance of HologramPlaceholder
     */
    public HologramPlaceholder() {
    }

    @Override
    public int getRefreshIntervalTicks() {
        return 10 * 20;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public @Nullable
    String getReplacement(@NotNull final Player player, @Nullable final String arguments) {
        try {
            if (arguments == null) {
                return "";
            }
            final Profile profile = PlayerConverter.getID(player);
            final String[] args = arguments.split(":", 2);
            return BetonQuest.getInstance().getVariableValue(args[0], "%" + args[1] + "%", profile);
        } catch (final Exception e) {
            LOG.warn("Could not parse hologram variable " + arguments + "! Expected format {bq:<package>:<variable>}");
            return arguments;
        }
    }
}
