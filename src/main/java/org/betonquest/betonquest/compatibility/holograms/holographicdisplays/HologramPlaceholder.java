package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.IndividualPlaceholder;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.util.PlayerConverter;
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
    private final VariableProcessor variableProcessor;

    /**
     * Creates new instance of HologramPlaceholder.
     *
     * @param log               the logger that will be used for logging
     * @param variableProcessor the processor to create new variables
     */
    public HologramPlaceholder(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        this.log = log;
        this.variableProcessor = variableProcessor;
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
        final Profile profile = PlayerConverter.getID(player);
        try {
            return variableProcessor.getValue(arguments, profile);
        } catch (final QuestException e) {
            log.warn("Could not parse hologram variable '" + arguments + "': " + e.getMessage(), e);
            return arguments;
        }
    }
}
