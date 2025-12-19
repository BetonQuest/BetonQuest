package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.IndividualPlaceholder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Variables;
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
     * The variable processor to use for creating the time variable.
     */
    private final Variables variables;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates new instance of HologramPlaceholder.
     *
     * @param log             the logger that will be used for logging
     * @param variables       the variable processor to create and resolve variables
     * @param profileProvider the profile provider instance
     */
    public HologramPlaceholder(final BetonQuestLogger log, final Variables variables,
                               final ProfileProvider profileProvider) {
        this.log = log;
        this.variables = variables;
        this.profileProvider = profileProvider;
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
        final Profile profile = profileProvider.getProfile(player);
        try {
            return variables.getValue(arguments, profile);
        } catch (final QuestException e) {
            log.warn("Could not parse hologram variable '" + arguments + "': " + e.getMessage(), e);
            return arguments;
        }
    }
}
