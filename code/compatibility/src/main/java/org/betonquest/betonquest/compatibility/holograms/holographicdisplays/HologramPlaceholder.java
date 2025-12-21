package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.IndividualPlaceholder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays placeholder <code>{bq:package:placeholder}</code>.
 */
public class HologramPlaceholder implements IndividualPlaceholder {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates new instance of HologramPlaceholder.
     *
     * @param log             the logger that will be used for logging
     * @param placeholders    the {@link Placeholders} to create and resolve placeholders
     * @param profileProvider the profile provider instance
     */
    public HologramPlaceholder(final BetonQuestLogger log, final Placeholders placeholders,
                               final ProfileProvider profileProvider) {
        this.log = log;
        this.placeholders = placeholders;
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
            return placeholders.getValue(arguments, profile);
        } catch (final QuestException e) {
            log.warn("Could not parse hologram placeholder '" + arguments + "': " + e.getMessage(), e);
            return arguments;
        }
    }
}
