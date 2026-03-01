package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.kernel.processor.PostLoadTask;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Hides and shows holograms to players, based on conditions at a fixed location.
 */
public class LocationHologramLoop extends HologramLoop implements PostLoadTask {

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     *
     * @param loggerFactory     the logger factory to use
     * @param log               the logger that will be used for logging
     * @param instructionApi    the instruction api to use
     * @param packManager       the quest package manager to get quest packages from
     * @param identifierFactory the identifier factory to create {@link HologramIdentifier}s for this type
     * @param configAccessor    the betonquest config accessor
     * @param hologramProvider  the hologram provider to create new holograms
     * @param plugin            the plugin to start tasks
     * @param textParser        the text parser used to parse text and colors
     * @param conditionManager  the condition manager
     * @param profileProvider   the profile provider
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public LocationHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                                final Instructions instructionApi, final QuestPackageManager packManager,
                                final IdentifierFactory<HologramIdentifier> identifierFactory, final ConfigAccessor configAccessor,
                                final HologramProvider hologramProvider, final Plugin plugin, final TextParser textParser,
                                final ConditionManager conditionManager, final ProfileProvider profileProvider) {
        super(loggerFactory, log, instructionApi, packManager, hologramProvider, "Hologram", "holograms",
                textParser, identifierFactory, configAccessor, conditionManager, profileProvider);
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
