package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

/**
 * Integrates with HolographicDisplays.
 */
public class HolographicDisplaysIntegrator extends HologramIntegrator {

    /**
     * The minimum required version of HolographicDisplays.
     */
    public static final String REQUIRED_VERSION = "3.0.0";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin instance to create Holograms.
     */
    private final Plugin plugin;

    /**
     * The placeholder manager to use.
     */
    private final PlaceholderProcessor placeholderProcessor;

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
     * @param instructionApi       the instruction api to use
     * @param identifierFactory    the identifier factory for placeholders
     * @param placeholderProcessor the placeholder manager to use
     */
    public HolographicDisplaysIntegrator(final BetonQuestLogger log, final Instructions instructionApi,
                                         final IdentifierFactory<PlaceholderIdentifier> identifierFactory,
                                         final PlaceholderProcessor placeholderProcessor) {
        super("HolographicDisplays", REQUIRED_VERSION);
        this.plugin = BetonQuest.getInstance();
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

    @Override
    public void enable(final BetonQuestApi api) {
        final HolographicDisplaysAPI holoApi = HolographicDisplaysAPI.get(plugin);
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        holoApi.registerIndividualPlaceholder("bq", new HologramPlaceholder(
                loggerFactory.create(HologramPlaceholder.class), placeholderProcessor, api.profiles()));
        holoApi.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder(
                loggerFactory.create(HologramGlobalPlaceholder.class), placeholderProcessor));
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
