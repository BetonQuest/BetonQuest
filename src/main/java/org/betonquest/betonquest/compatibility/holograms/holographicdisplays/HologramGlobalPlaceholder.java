package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.GlobalPlaceholder;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays global placeholder <code>{bqg:package:variable}</code>.
 */
public class HologramGlobalPlaceholder implements GlobalPlaceholder {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The variable processor to use for creating the time variable.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates new instance of HologramGlobalPlaceholder.
     *
     * @param log               the logger that will be used for logging
     * @param variableProcessor the processor to create new variables
     */
    public HologramGlobalPlaceholder(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        this.log = log;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public int getRefreshIntervalTicks() {
        return 10 * 20;
    }

    @Override
    @Nullable
    public String getReplacement(@Nullable final String arguments) {
        if (arguments == null) {
            return "";
        }
        try {
            return variableProcessor.getValue(arguments, null);
        } catch (final QuestException e) {
            log.warn("Could not parse hologram variable '" + arguments + "': " + e.getMessage(), e);
            return arguments;
        }
    }
}
