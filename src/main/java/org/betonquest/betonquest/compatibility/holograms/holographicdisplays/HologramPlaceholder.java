package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.IndividualPlaceholder;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays placeholder <code>{bq:package:variable}</code>.
 */
public class HologramPlaceholder implements IndividualPlaceholder {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(HologramPlaceholder.class);

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
    public @Nullable
    String getReplacement(@NotNull final Player player, @Nullable final String arguments) {
        if (arguments == null) {
            return "";
        }
        final Profile profile = PlayerConverter.getID(player);
        final int limit = 2;
        final String[] args = arguments.split(":", limit);
        if (args.length == limit) {
            return BetonQuest.getInstance().getVariableValue(args[0], "%" + args[1] + "%", profile);
        }
        LOG.warn("Could not parse hologram variable " + arguments + "! Expected format %<package>.<variable>%");
        return arguments;
    }
}
