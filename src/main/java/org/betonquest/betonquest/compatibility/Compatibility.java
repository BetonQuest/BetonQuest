package org.betonquest.betonquest.compatibility;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.aureliumskills.AureliumSkillsIntegrator;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensIntegrator;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegrator;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegrator;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegrator;
import org.betonquest.betonquest.compatibility.holographicdisplays.HolographicDisplaysIntegrator;
import org.betonquest.betonquest.compatibility.jobsreborn.JobsRebornIntegrator;
import org.betonquest.betonquest.compatibility.magic.MagicIntegrator;
import org.betonquest.betonquest.compatibility.mcmmo.McMMOIntegrator;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegrator;
import org.betonquest.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegrator;
import org.betonquest.betonquest.compatibility.protocollib.ProtocolLibIntegrator;
import org.betonquest.betonquest.compatibility.quests.QuestsIntegrator;
import org.betonquest.betonquest.compatibility.shopkeepers.ShopkeepersIntegrator;
import org.betonquest.betonquest.compatibility.skillapi.SkillAPIIntegrator;
import org.betonquest.betonquest.compatibility.skript.SkriptIntegrator;
import org.betonquest.betonquest.compatibility.vault.VaultIntegrator;
import org.betonquest.betonquest.compatibility.worldedit.WorldEditIntegrator;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Compatibility with other plugins
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
@CustomLog
public class Compatibility implements Listener {

    private static Compatibility instance;
    private final Map<String, Integrator> integrators = new HashMap<>();
    private final BetonQuest betonQuest = BetonQuest.getInstance();
    private final List<String> hooked = new ArrayList<>();

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public Compatibility() {
        instance = this;

        integrators.put("MythicMobs", new MythicMobsIntegrator());
        integrators.put("Citizens", new CitizensIntegrator());
        integrators.put("Vault", new VaultIntegrator());
        integrators.put("Skript", new SkriptIntegrator());
        integrators.put("WorldGuard", new WorldGuardIntegrator());
        integrators.put("WorldEdit", new WorldEditIntegrator());
        integrators.put("FastAsyncWorldEdit", new WorldEditIntegrator());
        integrators.put("mcMMO", new McMMOIntegrator());
        integrators.put("EffectLib", new EffectLibIntegrator());
        integrators.put("Heroes", new HeroesIntegrator());
        integrators.put("Magic", new MagicIntegrator());
        integrators.put("Denizen", new DenizenIntegrator());
        integrators.put("SkillAPI", new SkillAPIIntegrator());
        integrators.put("Quests", new QuestsIntegrator());
        integrators.put("Shopkeepers", new ShopkeepersIntegrator());
        integrators.put("PlaceholderAPI", new PlaceholderAPIIntegrator());
        integrators.put("HolographicDisplays", new HolographicDisplaysIntegrator());
        integrators.put("ProtocolLib", new ProtocolLibIntegrator());
        integrators.put("Brewery", new BreweryIntegrator());
        integrators.put("Jobs", new JobsRebornIntegrator());
        integrators.put("AureliumSkills", new AureliumSkillsIntegrator());

        // hook into already enabled plugins in case Bukkit messes up the loading order
        for (final Plugin hook : Bukkit.getPluginManager().getPlugins()) {
            hook(hook);
        }

        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        new BukkitRunnable() {
            @Override
            public void run() {
                // log which plugins have been hooked
                if (!hooked.isEmpty()) {
                    final StringBuilder string = new StringBuilder();
                    for (final String plugin : hooked) {
                        string.append(plugin).append(", ");
                    }
                    final String plugins = string.substring(0, string.length() - 2);
                    LOG.info(null, "Hooked into " + plugins + "!");
                }
            }
        }.runTask(BetonQuest.getInstance());

    }

    /**
     * @return the list of hooked plugins
     */
    public static List<String> getHooked() {
        return instance.hooked;
    }

    public static void reload() {
        for (final String hooked : getHooked()) {
            instance.integrators.get(hooked).reload();
        }
    }

    public static void disable() {
        for (final String hooked : getHooked()) {
            instance.integrators.get(hooked).close();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPluginEnable(final PluginEnableEvent event) {
        hook(event.getPlugin());
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void hook(final Plugin hookedPlugin) {

        // don't want to hook twice
        if (hooked.contains(hookedPlugin.getName())) {
            return;
        }

        // don't want to hook into disabled plugins
        if (!hookedPlugin.isEnabled()) {
            return;
        }

        final String name = hookedPlugin.getName();
        final Integrator integrator = integrators.get(name);

        // this plugin is not an integration
        if (integrator == null) {
            return;
        }

        // hook into the plugin if it's enabled in the config
        if ("true".equalsIgnoreCase(betonQuest.getConfig().getString("hook." + name.toLowerCase(Locale.ROOT)))) {
            LOG.info(null, "Hooking into " + name);

            // log important information in case of an error
            try {
                integrator.hook();
                hooked.add(name);
            } catch (final HookException exception) {
                final String message = String.format("Could not hook into %s %s! %s",
                        hookedPlugin.getName(),
                        hookedPlugin.getDescription().getVersion(),
                        exception.getMessage());
                LOG.warning(null, message, exception);
                LOG.warning(null, "BetonQuest will work correctly, except for that single integration. "
                        + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                        + "' to false in config.yml file.");
            } catch (final RuntimeException | LinkageError exception) {
                final String message = String.format("There was an unexpected error while hooking into %s %s (BetonQuest %s, Spigot %s)! %s",
                        hookedPlugin.getName(),
                        hookedPlugin.getDescription().getVersion(),
                        BetonQuest.getInstance().getDescription().getVersion(),
                        Bukkit.getVersion(),
                        exception.getMessage());
                LOG.error(null, message, exception);
                LOG.warning(null, "BetonQuest will work correctly, except for that single integration. "
                        + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                        + "' to false in config.yml file.");
            }
        }
    }
}
