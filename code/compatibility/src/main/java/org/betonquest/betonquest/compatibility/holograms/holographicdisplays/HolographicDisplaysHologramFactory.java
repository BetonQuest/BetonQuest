package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
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
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

/**
 * Hologram Creator implementation for HolographicDisplays.
 */
public class HolographicDisplaysHologramFactory implements BetonHologramFactory {

    /**
     * The plugin instance to create Holograms.
     */
    private final Plugin plugin;

    /**
     * The placeholder manager to use.
     */
    private final PlaceholderProcessor placeholderProcessor;

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
     * Creates a new HolographicDisplaysIntegrator for HolographicDisplays.
     *
     * @param log                  the custom logger for this class
     * @param plugin               the plugin instance to create holograms
     * @param instructionApi       the instruction api to use
     * @param identifierFactory    the identifier factory for placeholders
     * @param placeholderProcessor the placeholder manager to use
     */
    public HolographicDisplaysHologramFactory(final BetonQuestLogger log, final Plugin plugin, final Instructions instructionApi,
                                              final IdentifierFactory<PlaceholderIdentifier> identifierFactory,
                                              final PlaceholderProcessor placeholderProcessor) {
        this.plugin = plugin;
        this.instructionApi = instructionApi;
        this.identifierFactory = identifierFactory;
        this.log = log;
        this.placeholderProcessor = placeholderProcessor;
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = HolographicDisplaysAPI.get(plugin).createHologram(location);
        hologram.setPlaceholderSetting(PlaceholderSetting.ENABLE_ALL);
        return new HolographicDisplaysHologram(hologram);
    }

    /**
     * Parses a package-specific BetonQuest placeholder and converts it to the HolographicDisplays API specific
     * placeholder format.
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
                final String prefix = placeholderProcessor.get(placeholderIdentifier).allowsPlayerless() ? "{bqg:" : "{bq:";
                return prefix + placeholderIdentifier.getPackage().getQuestPath() + ":" + instruction + "}";
            } catch (final QuestException exception) {
                log.warn("Could not create placeholder '" + group + "': " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
