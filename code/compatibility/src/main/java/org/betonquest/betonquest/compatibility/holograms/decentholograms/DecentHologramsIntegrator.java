package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.placeholder.PlaceholderID;
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
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Creates a new DecentHologramsIntegrator for DecentHolograms.
     *
     * @param log          the custom logger for this class
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     */
    public DecentHologramsIntegrator(final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager) {
        super("DecentHolograms", "2.7.5");
        this.log = log;
        this.placeholders = placeholders;
        this.packManager = packManager;
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
     * Parses a BetonQuest placeholder with package and converts it to the appropriate format for DecentHolograms
     * which uses PlaceholderAPI format.
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
                final PlaceholderID placeholderID = new PlaceholderID(placeholders, packManager, pack, group);
                final Instruction instruction = placeholderID.getInstruction();
                return "%betonquest_" + placeholderID.getPackage().getQuestPath() + ":" + instruction + "%";
            } catch (final QuestException exception) {
                log.warn("Could not create placeholder '" + group + "': " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
