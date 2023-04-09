package org.betonquest.betonquest.compatibility.placeholderapi;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
@SuppressFBWarnings("HE_INHERITS_EQUALS_USE_HASHCODE")
public class BetonQuestPlaceholder extends PlaceholderExpansion {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(BetonQuestPlaceholder.class, "PlaceholderAPI Integration");

    public BetonQuestPlaceholder() {
        super();
    }

    /**
     * Persist through reloads
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * We can always register
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Name of person who created the expansion
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return BetonQuest.getInstance().getDescription().getAuthors().toString();
    }

    /**
     * The identifier for PlaceHolderAPI to link to this expansion
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "betonquest";
    }

    /**
     * Version of the expansion
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return BetonQuest.getInstance().getDescription().getVersion();
    }

    /**
     * A placeholder request has occurred and needs a value
     *
     * @param player     A potentially null {@link org.bukkit.entity.Player Player}.
     * @param identifier A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public @NotNull String onPlaceholderRequest(final @Nullable Player player, final @NotNull String identifier) {
        final String pack;
        final String placeholderIdentifier;
        final int index = identifier.indexOf(':');
        if (index == -1) {
            LOG.warn("Variable without explicit package requested through PAPI: '%s'".formatted(identifier));
            return "";
        } else {
            pack = identifier.substring(0, index);
            placeholderIdentifier = identifier.substring(index + 1);
        }
        final OnlineProfile onlineProfile = player == null ? null : PlayerConverter.getID(player);
        return BetonQuest.getInstance().getVariableValue(pack, '%' + placeholderIdentifier + '%', onlineProfile);
    }
}
