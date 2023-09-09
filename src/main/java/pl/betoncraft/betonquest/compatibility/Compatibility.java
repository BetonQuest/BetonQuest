package pl.betoncraft.betonquest.compatibility;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.brewery.BreweryIntegrator;
import pl.betoncraft.betonquest.compatibility.citizens.CitizensIntegrator;
import pl.betoncraft.betonquest.compatibility.denizen.DenizenIntegrator;
import pl.betoncraft.betonquest.compatibility.effectlib.EffectLibIntegrator;
import pl.betoncraft.betonquest.compatibility.heroes.HeroesIntegrator;
import pl.betoncraft.betonquest.compatibility.holographicdisplays.HolographicDisplaysIntegrator;
import pl.betoncraft.betonquest.compatibility.jobsreborn.JobsRebornIntegrator;
import pl.betoncraft.betonquest.compatibility.magic.MagicIntegrator;
import pl.betoncraft.betonquest.compatibility.mcmmo.McMMOIntegrator;
import pl.betoncraft.betonquest.compatibility.mmogroup.mmocore.MMOCoreIntegrator;
import pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems.MMOItemsIntegrator;
import pl.betoncraft.betonquest.compatibility.mmogroup.mmolib.MythicLibIntegrator;
import pl.betoncraft.betonquest.compatibility.mythicmobs.MythicMobsIntegrator;
import pl.betoncraft.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegrator;
import pl.betoncraft.betonquest.compatibility.protocollib.ProtocolLibIntegrator;
import pl.betoncraft.betonquest.compatibility.quests.QuestsIntegrator;
import pl.betoncraft.betonquest.compatibility.shopkeepers.ShopkeepersIntegrator;
import pl.betoncraft.betonquest.compatibility.skillapi.SkillAPIIntegrator;
import pl.betoncraft.betonquest.compatibility.skript.SkriptIntegrator;
import pl.betoncraft.betonquest.compatibility.vault.VaultIntegrator;
import pl.betoncraft.betonquest.compatibility.worldedit.WorldEditIntegrator;
import pl.betoncraft.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import pl.betoncraft.betonquest.exceptions.HookException;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

/**
 * Compatibility with other plugins
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
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
        integrators.put("MMOLib", new MythicLibIntegrator());
        integrators.put("MMOCore", new MMOCoreIntegrator());
        integrators.put("MMOItems", new MMOItemsIntegrator());
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
                    LogUtils.getLogger().log(Level.INFO, "Hooked into " + plugins + "!");
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
            LogUtils.getLogger().log(Level.INFO, "Hooking into " + name);

            // log important information in case of an error
            try {
                integrator.hook();
                hooked.add(name);
            } catch (final HookException exception) {
                final String message = String.format("Could not hook into %s %s! %s",
                        hookedPlugin.getName(),
                        hookedPlugin.getDescription().getVersion(),
                        exception.getMessage());
                LogUtils.getLogger().log(Level.WARNING, message);
                LogUtils.getLogger().log(Level.FINE, message, exception);
                LogUtils.getLogger().log(Level.WARNING, "BetonQuest will work correctly, except for that single integration. "
                        + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                        + "' to false in config.yml file.");
            } catch (final RuntimeException | LinkageError exception) {
                final String message = String.format("There was an unexpected error while hooking into %s %s (BetonQuest %s, Spigot %s)! %s",
                        hookedPlugin.getName(),
                        hookedPlugin.getDescription().getVersion(),
                        BetonQuest.getInstance().getDescription().getVersion(),
                        Bukkit.getVersion(),
                        exception.getMessage());
                LogUtils.getLogger().log(Level.WARNING, message, exception);
                LogUtils.getLogger().log(Level.WARNING, "BetonQuest will work correctly, except for that single integration. "
                        + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                        + "' to false in config.yml file.");
            }
        }
    }
}
