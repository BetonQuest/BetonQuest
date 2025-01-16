package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.regex.Matcher;

/**
 * Integrates with HolographicDisplays.
 */
public class HolographicDisplaysIntegrator extends HologramIntegrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new HolographicDisplaysIntegrator for HolographicDisplays.
     */
    public HolographicDisplaysIntegrator() {
        super("HolographicDisplays", "3.0.0", "SNAPSHOT-b");
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = HolographicDisplaysAPI.get(BetonQuest.getInstance()).createHologram(location);
        hologram.setPlaceholderSetting(PlaceholderSetting.ENABLE_ALL);
        return new HolographicDisplaysHologram(hologram);
    }

    @Override
    public void hook() throws HookException {
        super.hook();
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            log.warn("Holograms from HolographicDisplays won't be able to hide from players without ProtocolLib plugin! "
                    + "Install it to use conditioned holograms.");
        }
        final BetonQuest instance = BetonQuest.getInstance();
        final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(instance);
        final BetonQuestLoggerFactory loggerFactory = instance.getLoggerFactory();
        api.registerIndividualPlaceholder("bq", new HologramPlaceholder(
                loggerFactory.create(HologramPlaceholder.class), instance.getVariableProcessor()));
        api.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder(
                loggerFactory.create(HologramGlobalPlaceholder.class), instance.getVariableProcessor()));
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack:objective.kills.left%" to
           "{bq:pack:objective.kills.left}" which is parsed by HolographicDisplays as a custom API placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final Variable variable = BetonQuest.getInstance().getVariableProcessor().create(pack, group);
                final Instruction instruction = variable.getInstruction();
                final String prefix = variable.isStaticness() ? "{bqg:" : "{bq:";
                return prefix + instruction.getPackage().getQuestPath() + ":" + instruction + "}";
            } catch (final QuestException exception) {
                log.warn("Could not create variable '" + group + "' variable: " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
