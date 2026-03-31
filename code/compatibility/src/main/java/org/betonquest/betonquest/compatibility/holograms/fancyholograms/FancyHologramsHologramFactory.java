package org.betonquest.betonquest.compatibility.holograms.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.bukkit.Location;

import java.util.regex.Matcher;

/**
 * Hologram Factory implementation for FancyHolograms.
 */
public class FancyHologramsHologramFactory implements BetonHologramFactory {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The instruction api to use.
     */
    private final Instructions instructionApi;

    /**
     * The identifier factory for placeholders.
     */
    private final IdentifierFactory<PlaceholderIdentifier> identifierFactory;

    /**
     * Creates a new {@link BetonHologramFactory} for DecentHolograms.
     *
     * @param log               the custom logger for this class
     * @param identifierFactory the identifier factory for placeholders
     * @param instructionApi    the instruction api to use
     */
    public FancyHologramsHologramFactory(final BetonQuestLogger log, final Instructions instructionApi,
                                         final IdentifierFactory<PlaceholderIdentifier> identifierFactory) {
        this.log = log;
        this.instructionApi = instructionApi;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        return new FancyHologramsHologram(FancyHologramsPlugin.get().getHologramManager(), location);
    }

    /**
     * Parses a package-specific BetonQuest placeholder and converts it to the PlaceholderAPI format since
     * FancyHolograms requires it.
     *
     * @param pack the quest pack where the placeholder resides
     * @param text the raw text
     * @return the parsed and formatted full string
     */
    @Override
    public String parsePlaceholder(final QuestPackage pack, final String text) {
        final Matcher matcher = HologramProvider.PLACEHOLDER_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final PlaceholderIdentifier placeholderIdentifier = identifierFactory.parseIdentifier(pack, group);
                final Instruction instruction = instructionApi.createPlaceholder(placeholderIdentifier, placeholderIdentifier.readRawInstruction());
                return "%betonquest_" + placeholderIdentifier.getPackage().getQuestPath() + ":" + instruction + "%";
            } catch (final QuestException exception) {
                log.warn("Could not create placeholder '" + group + "': " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
