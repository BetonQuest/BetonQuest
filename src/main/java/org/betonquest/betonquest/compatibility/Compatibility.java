package org.betonquest.betonquest.compatibility;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.auraskills.AuraSkillsIntegrator;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensIntegrator;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegrator;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegrator;
import org.betonquest.betonquest.compatibility.fabled.FabledIntegrator;
import org.betonquest.betonquest.compatibility.fakeblock.FakeBlockIntegrator;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.compatibility.holograms.decentholograms.DecentHologramsIntegrator;
import org.betonquest.betonquest.compatibility.holograms.holographicdisplays.HolographicDisplaysIntegrator;
import org.betonquest.betonquest.compatibility.jobsreborn.JobsRebornIntegrator;
import org.betonquest.betonquest.compatibility.luckperms.LuckPermsIntegrator;
import org.betonquest.betonquest.compatibility.magic.MagicIntegrator;
import org.betonquest.betonquest.compatibility.mcmmo.McMMOIntegrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOCoreIntegrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsIntegrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmolib.MythicLibIntegrator;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegrator;
import org.betonquest.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegrator;
import org.betonquest.betonquest.compatibility.protocollib.ProtocolLibIntegrator;
import org.betonquest.betonquest.compatibility.quests.QuestsIntegrator;
import org.betonquest.betonquest.compatibility.redischat.RedisChatIntegrator;
import org.betonquest.betonquest.compatibility.shopkeepers.ShopkeepersIntegrator;
import org.betonquest.betonquest.compatibility.skript.SkriptIntegrator;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsIntegrator;
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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Loads compatibility with other plugins.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.AssignmentToNonFinalStatic"})
public class Compatibility implements Listener {
    /**
     * An instance of this class.
     */
    @SuppressWarnings("NullAway.Init")
    private static Compatibility instance;

    /**
     * BetonQuest plugin instance for tasks and configs.
     */
    private final BetonQuest betonQuest;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * A map of all integrators.
     * The key is the name of the plugin, the value a pair of the integrator class and an instance of it.
     * The instance must only exist if the plugin was hooked.
     */
    private final Map<String, Pair<Class<? extends Integrator>, Integrator>> integrators = new HashMap<>();

    /**
     * Loads all compatibility with other plugins that is available in the current runtime.
     *
     * @param betonQuest the BetonQuest plugin instance for tasks and configs
     * @param log        the custom logger for this class
     */
    public Compatibility(final BetonQuest betonQuest, final BetonQuestLogger log) {
        this.betonQuest = betonQuest;
        this.log = log;
        instance = this;

        registerCompatiblePlugins();

        Bukkit.getPluginManager().registerEvents(this, betonQuest);

        // Integrate already enabled plugins in case Bukkit messes up the loading order
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            integratePlugin(plugin);
        }

        //Must be called after all plugins have been integrated
        HologramProvider.init();

        //Delay after server start to finish all hooking first
        new BukkitRunnable() {
            @Override
            public void run() {
                final String hooks = buildHookedPluginsMessage();
                if (!hooks.isEmpty()) {
                    log.info("Enabled compatibility for " + hooks + "!");
                }
            }
        }.runTask(betonQuest);
    }

    /**
     * @return the list of hooked plugins
     */
    public static List<String> getHooked() {
        return instance.integrators.entrySet().stream().filter(entry -> entry.getValue().getRight() != null)
                .map(Map.Entry::getKey).toList();
    }

    /**
     * After all integrations are successfully hooked,
     * this method can be called to activate cross compatibility features.
     */
    public static void postHook() {
        instance.integrators.values().stream()
                .filter(pair -> pair.getRight() != null)
                .forEach(pair -> {
                    final Integrator integrator = pair.getRight();
                    try {
                        integrator.postHook();
                    } catch (final HookException e) {
                        instance.log.warn("Error while enabling some features while post hooking into " + pair.getLeft()
                                + " reason: " + e.getMessage(), e);
                    }
                });
    }

    /**
     * Reloads all loaded integrators.
     */
    public static void reload() {
        instance.integrators.values().stream()
                .map(Pair::getRight)
                .filter(Objects::nonNull)
                .forEach(Integrator::reload);
    }

    /**
     * Disables all loaded integrators.
     */
    public static void disable() {
        if (instance != null) {
            instance.integrators.values().stream()
                    .map(Pair::getRight)
                    .filter(Objects::nonNull)
                    .forEach(Integrator::close);
        }
    }

    private String buildHookedPluginsMessage() {
        return String.join(", ", getHooked());
    }

    /**
     * Triggers the integration of a plugin.
     *
     * @param event the event to listen for
     */
    @EventHandler(ignoreCancelled = true)
    public void onPluginEnable(final PluginEnableEvent event) {
        integratePlugin(event.getPlugin());
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void integratePlugin(final Plugin hookedPlugin) {
        if (!hookedPlugin.isEnabled()) {
            return;
        }
        final String name = hookedPlugin.getName();
        if (!integrators.containsKey(name) || integrators.get(name).getRight() != null) {
            return;
        }

        final boolean isEnabled = betonQuest.getPluginConfig().getBoolean("hook." + name.toLowerCase(Locale.ROOT));
        if (!isEnabled) {
            log.debug("Did not hook " + name + " because it is disabled");
            return;
        }

        final Class<? extends Integrator> integratorClass = integrators.get(name).getKey();
        final Integrator integrator;
        try {
            integrator = integratorClass.getConstructor().newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException
                       | NoSuchMethodException | NoClassDefFoundError e) {
            log.warn("Error while integrating " + name + " with version " + hookedPlugin.getDescription().getVersion() + ": " + e, e);
            log.warn("You are likely running an incompatible version of " + name + ".");
            return;
        }

        log.info("Hooking into " + name);

        try {
            integrator.hook();
            integrators.get(name).setValue(integrator);
        } catch (final HookException exception) {
            final String message = String.format("Could not hook into %s %s! %s",
                    hookedPlugin.getName(),
                    hookedPlugin.getDescription().getVersion(),
                    exception.getMessage());
            log.warn(message, exception);
            log.warn("BetonQuest will work correctly, except for that single integration. "
                    + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                    + "' to false in config.yml file.");
        } catch (final RuntimeException | LinkageError exception) {
            final String message = String.format("There was an unexpected error while hooking into %s %s (BetonQuest %s, Spigot %s)! %s",
                    hookedPlugin.getName(),
                    hookedPlugin.getDescription().getVersion(),
                    betonQuest.getDescription().getVersion(),
                    Bukkit.getVersion(),
                    exception.getMessage());
            log.error(message, exception);
            log.warn("BetonQuest will work correctly, except for that single integration. "
                    + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                    + "' to false in config.yml file.");
        }
    }

    private void registerCompatiblePlugins() {
        register("MythicMobs", MythicMobsIntegrator.class);
        register("Citizens", CitizensIntegrator.class);
        register("Vault", VaultIntegrator.class);
        register("Skript", SkriptIntegrator.class);
        register("WorldGuard", WorldGuardIntegrator.class);
        register("WorldEdit", WorldEditIntegrator.class);
        register("FastAsyncWorldEdit", WorldEditIntegrator.class);
        register("mcMMO", McMMOIntegrator.class);
        register("MythicLib", MythicLibIntegrator.class);
        register("MMOCore", MMOCoreIntegrator.class);
        register("MMOItems", MMOItemsIntegrator.class);
        register("EffectLib", EffectLibIntegrator.class);
        register("Heroes", HeroesIntegrator.class);
        register("Magic", MagicIntegrator.class);
        register("Denizen", DenizenIntegrator.class);
        register("Fabled", FabledIntegrator.class);
        register("Quests", QuestsIntegrator.class);
        register("Shopkeepers", ShopkeepersIntegrator.class);
        register("PlaceholderAPI", PlaceholderAPIIntegrator.class);
        register("ProtocolLib", ProtocolLibIntegrator.class);
        register("Brewery", BreweryIntegrator.class);
        register("BreweryX", BreweryIntegrator.class);
        register("Jobs", JobsRebornIntegrator.class);
        register("LuckPerms", LuckPermsIntegrator.class);
        register("AuraSkills", AuraSkillsIntegrator.class);
        register("DecentHolograms", DecentHologramsIntegrator.class);
        register("HolographicDisplays", HolographicDisplaysIntegrator.class);
        register("fake-block", FakeBlockIntegrator.class);
        register("RedisChat", RedisChatIntegrator.class);
        register("Train_Carts", TrainCartsIntegrator.class);
    }

    private void register(final String name, final Class<? extends Integrator> integrator) {
        integrators.put(name, new MutablePair<>(integrator, null));
    }
}
