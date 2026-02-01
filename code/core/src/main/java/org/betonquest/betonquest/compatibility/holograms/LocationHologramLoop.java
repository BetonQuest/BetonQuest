package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.kernel.processor.StartTask;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Hides and shows holograms to players, based on conditions at a fixed location.
 */
public class LocationHologramLoop extends HologramLoop implements StartTask {

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     *
     * @param loggerFactory     logger factory to use
     * @param log               the logger that will be used for logging
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param instructionApi    the instruction api to use
     * @param packManager       the quest package manager to get quest packages from
     * @param identifierFactory the identifier factory to create {@link HologramIdentifier}s for this type
     * @param hologramProvider  the hologram provider to create new holograms
     * @param plugin            the plugin to start tasks
     * @param textParser        the text parser used to parse text and colors
     * @param parsers           the argument parsers
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public LocationHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                                final Placeholders placeholders, final InstructionApi instructionApi, final QuestPackageManager packManager,
                                final IdentifierFactory<HologramIdentifier> identifierFactory,
                                final HologramProvider hologramProvider, final Plugin plugin, final TextParser textParser,
                                final ArgumentParsers parsers) {
        super(loggerFactory, log, placeholders, instructionApi, packManager, hologramProvider, "Hologram", "holograms",
                textParser, parsers, identifierFactory);
        this.plugin = plugin;
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final SectionInstruction instruction) throws QuestException {
        final Location location = instruction.read().value("location").location().get().getValue(null);
        final List<BetonHologram> holograms = new ArrayList<>();
        holograms.add(hologramProvider.createHologram(location));
        return holograms;
    }

    @Override
    public void startAll() {
        HologramRunner.start(plugin);
    }
}
