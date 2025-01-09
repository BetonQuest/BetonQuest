package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.QuestException;
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
     * Creates a new DecentHologramsIntegrator for DecentHolograms.
     */
    public DecentHologramsIntegrator() {
        super("DecentHolograms", "2.7.5");
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location);
        hologram.enable();
        return new DecentHologramsHologram(hologram);
    }

    @Override
    public void hook() throws HookException {
        super.hook();
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.warn("Holograms from DecentHolograms will not be able to use BetonQuest variables in text lines "
                    + "without PlaceholderAPI plugin! Install it to use holograms with variables!");
        }
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack.objective.kills.left%" to
           "%betonquest_pack:objective.kills.left%" which is parsed by DecentHolograms as a PlaceholderAPI placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final Variable variable = BetonQuest.getInstance().getVariableProcessor().create(pack, group);
                final Instruction instruction = variable.getInstruction();
                return "%betonquest_" + instruction.getPackage().getQuestPath() + ":" + instruction.getInstruction() + "%";
            } catch (final QuestException exception) {
                log.warn("Could not create variable '" + group + "' variable: " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
