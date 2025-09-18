package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.VariableID;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
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
     * The BetonQuest instance.
     */
    private final BetonQuest plugin;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Creates a new HolographicDisplaysIntegrator for HolographicDisplays.
     *
     * @param packManager the quest package manager to get quest packages from
     */
    public HolographicDisplaysIntegrator(final QuestPackageManager packManager) {
        super("HolographicDisplays", "3.0.0", "SNAPSHOT-b");
        this.plugin = BetonQuest.getInstance();
        this.packManager = packManager;
        this.log = plugin.getLoggerFactory().create(getClass());
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = HolographicDisplaysAPI.get(plugin).createHologram(location);
        hologram.setPlaceholderSetting(PlaceholderSetting.ENABLE_ALL);
        return new HolographicDisplaysHologram(hologram);
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        super.hook(api);
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            log.warn("Holograms from HolographicDisplays won't be able to hide from players without ProtocolLib plugin! "
                    + "Install it to use conditioned holograms.");
        }
        final HolographicDisplaysAPI holoApi = HolographicDisplaysAPI.get(plugin);
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        holoApi.registerIndividualPlaceholder("bq", new HologramPlaceholder(
                loggerFactory.create(HologramPlaceholder.class), plugin.getVariableProcessor()));
        holoApi.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder(
                loggerFactory.create(HologramGlobalPlaceholder.class), plugin.getVariableProcessor()));
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack:objective.kills.left%" to
           "{bq:pack:objective.kills.left}" which is parsed by HolographicDisplays as a custom API placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final VariableID variable = new VariableID(packManager, pack, group);
                final Instruction instruction = variable.getInstruction();
                final String prefix = plugin.getVariableProcessor().get(variable).allowsPlayerless() ? "{bqg:" : "{bq:";
                return prefix + variable.getPackage().getQuestPath() + ":" + instruction + "}";
            } catch (final QuestException exception) {
                log.warn("Could not create variable '" + group + "' variable: " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
