package org.betonquest.betonquest.compatibility.placeholderapi;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.PlayerConverter;
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
     * The variable processor to use for creating the placeholder variables.
     */
    private final VariableProcessor variableProcessor;

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
     * @param log               the custom logger for this class
     * @param variableProcessor the processor to create new variables
     * @param authors           the combined author string
     * @param version           the version string
     */
    public BetonQuestPlaceholder(final BetonQuestLogger log, final VariableProcessor variableProcessor,
                                 final String authors, final String version) {
        super();
        this.log = log;
        this.variableProcessor = variableProcessor;
        this.authors = authors;
        this.version = version;
    }

    /**
     * Persist through reloads.
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * We can always register.
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Name of person who created the expansion.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return authors;
    }

    /**
     * The identifier for PlaceHolderAPI to link to this expansion.
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "betonquest";
    }

    /**
     * Version of the expansion.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * A placeholder request has occurred and needs a value.
     *
     * @param player     A potentially null {@link org.bukkit.entity.Player Player}.
     * @param identifier A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(@Nullable final Player player, final String identifier) {
        final Profile profile = player == null ? null : PlayerConverter.getID(player);
        try {
            return variableProcessor.getValue(identifier, profile);
        } catch (final QuestException e) {
            log.warn("Could not parse through PAPI requested variable: " + identifier, e);
            return "";
        }
    }
}
