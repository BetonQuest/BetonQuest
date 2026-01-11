package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;
import java.util.regex.Matcher;

/**
 * Integrates with DecentHolograms.
 */
public class DecentHologramsIntegrator extends HologramIntegrator {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The identifier factory for placeholders.
     */
    private final IdentifierFactory<PlaceholderIdentifier> identifierFactory;

    /**
     * The instruction api to use.
     */
    private final InstructionApi instructionApi;

    /**
     * Creates a new DecentHologramsIntegrator for DecentHolograms.
     *
     * @param log               the custom logger for this class
     * @param identifierFactory the identifier factory for placeholders
     * @param instructionApi    the instruction api to use
     */
    public DecentHologramsIntegrator(final BetonQuestLogger log, final IdentifierFactory<PlaceholderIdentifier> identifierFactory, final InstructionApi instructionApi) {
        super("DecentHolograms", "2.7.5");
        this.log = log;
        this.identifierFactory = identifierFactory;
        this.instructionApi = instructionApi;
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location);
        hologram.enable();
        return new DecentHologramsHologram(hologram);
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        super.hook(api);
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.warn("Holograms from DecentHolograms will not be able to use BetonQuest placeholders in text lines "
                    + "without PlaceholderAPI plugin! Install it to use holograms with placeholders!");
        }
    }

    /**
     * Parses a package-specific BetonQuest placeholder and converts it to the PlaceholderAPI format since
     * DecentHolograms requires it.
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
                final Instruction instruction = instructionApi.createInstruction(placeholderIdentifier, placeholderIdentifier.readRawInstruction());
                return "%betonquest_" + placeholderIdentifier.getPackage().getQuestPath() + ":" + instruction + "%";
            } catch (final QuestException exception) {
                log.warn("Could not create placeholder '" + group + "': " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
