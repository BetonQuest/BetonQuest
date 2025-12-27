package org.betonquest.betonquest.compatibility.placeholderapi;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The BetonQuest PAPI Placeholder.
 */
@SuppressFBWarnings("HE_INHERITS_EQUALS_USE_HASHCODE")
public class BetonQuestPlaceholder extends PlaceholderExpansion {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The combined authors.
     */
    private final String authors;

    /**
     * The version string.
     */
    private final String version;

    /**
     * Create a new BetonQuest PAPI Placeholder.
     *
     * @param log             the custom logger for this class
     * @param profileProvider the profile provider instance
     * @param placeholders    the {@link Placeholders} to create and resolve placeholders
     * @param authors         the combined author string
     * @param version         the version string
     */
    public BetonQuestPlaceholder(final BetonQuestLogger log, final ProfileProvider profileProvider, final Placeholders placeholders,
                                 final String authors, final String version) {
        super();
        this.log = log;
        this.profileProvider = profileProvider;
        this.placeholders = placeholders;
        this.authors = authors;
        this.version = version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return authors;
    }

    @Override
    public String getIdentifier() {
        return "betonquest";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String onPlaceholderRequest(@Nullable final Player player, final String identifier) {
        final Profile profile = player == null ? null : profileProvider.getProfile(player);
        try {
            return placeholders.getValue(identifier, profile);
        } catch (final QuestException e) {
            log.warn("Could not parse through PAPI requested placeholder: " + identifier, e);
            return "";
        }
    }
}
