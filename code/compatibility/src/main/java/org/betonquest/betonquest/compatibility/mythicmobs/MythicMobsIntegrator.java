package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.mythicmobs.action.MythicCastSkillActionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.action.MythicSpawnMobActionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.condition.MythicMobDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.item.MythicItemFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.item.MythicQuestItemSerializer;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsInteractCatcher;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsReverseIdentifier;
import org.betonquest.betonquest.compatibility.mythicmobs.objective.MythicMobKillObjectiveFactory;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for MythicMobs.
 */
public class MythicMobsIntegrator implements Integrator {

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Active MythicHider instance.
     */
    @Nullable
    private MythicHider mythicHider;

    /**
     * The default constructor.
     */
    public MythicMobsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @SuppressWarnings("PMD.CloseResource")
    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        validateVersion();

        final MythicBukkit mythicBukkit = MythicBukkit.inst();
        final BukkitAPIHelper apiHelper = mythicBukkit.getAPIHelper();
        final MobExecutor mobExecutor = mythicBukkit.getMobManager();

        final PluginManager manager = plugin.getServer().getPluginManager();
        mythicHider = new MythicHider(api.profiles(), plugin);
        mythicHider.reload(plugin.getPluginConfig().getInt("hider.npc_update_interval", 5 * 20));
        manager.registerEvents(mythicHider, plugin);

        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        api.conditions().registry().register("mythicmobdistance", new MythicMobDistanceConditionFactory(mobExecutor, new MythicMobParser(mobExecutor)));
        api.objectives().registry().register("mmobkill", new MythicMobKillObjectiveFactory());
        api.actions().registry().registerCombined("mspawnmob", new MythicSpawnMobActionFactory(
                new MythicMobDoubleParser(mobExecutor), plugin, mythicHider));
        api.actions().registry().register("mcast", new MythicCastSkillActionFactory(loggerFactory, apiHelper));

        final NpcRegistry npcRegistry = api.npcs().registry();
        final Listener interactCatcher = new MythicMobsInteractCatcher(api.profiles(), npcRegistry, mobExecutor, mythicHider);
        final MythicMobsNpcFactory npcFactory = new MythicMobsNpcFactory(mobExecutor, mythicHider);
        manager.registerEvents(new DynamicListenerRegister(interactCatcher, npcFactory), plugin);
        npcRegistry.register("mythicmobs", npcFactory);
        npcRegistry.registerIdentifier(new MythicMobsReverseIdentifier());

        final ItemRegistry itemRegistry = api.items().registry();
        final ItemExecutor itemManager = mythicBukkit.getItemManager();
        itemRegistry.register("mythic", new MythicItemFactory(itemManager));
        itemRegistry.registerSerializer("mythic", new MythicQuestItemSerializer(itemManager));
    }

    /**
     * Aborts the hooking process if the installed version of MythicMobs is invalid.
     *
     * @throws UnsupportedVersionException if the installed version of MythicMobs is < 5.0.0.
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        final String versionWithCommit = mythicMobs.getDescription().getVersion();
        final String[] parts = versionWithCommit.split("-");
        final Version mythicMobsVersion = new Version(parts[0]);
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "-");
        if (comparator.isOlderThan(mythicMobsVersion, new Version("5.0.0"))) {
            throw new UnsupportedVersionException(mythicMobs, "5.0.0+");
        }
    }

    @Override
    public void reload() {
        if (mythicHider != null) {
            mythicHider.reload(plugin.getPluginConfig().getInt("hider.npc_update_interval", 5 * 20));
        }
    }

    @Override
    public void close() {
        if (mythicHider != null) {
            mythicHider.stop();
            HandlerList.unregisterAll(mythicHider);
        }
    }

    /**
     * Handles de-/registration of a listener based on a condition which may change on reload.
     */
    public final class DynamicListenerRegister implements Listener {

        /**
         * Listener to de-/register.
         */
        private final Listener listener;

        /**
         * Factory to determine if listener should be registered.
         */
        private final MythicMobsNpcFactory npcFactory;

        /**
         * If the listener is currently registered.
         */
        private boolean registered;

        private DynamicListenerRegister(final Listener listener, final MythicMobsNpcFactory npcFactory) {
            this.listener = listener;
            this.npcFactory = npcFactory;
        }

        /**
         * Registers or unregisters the listener after BetonQuest reload.
         *
         * @param event the load data event on reload
         */
        @EventHandler
        public void onReloadData(final LoadDataEvent event) {
            if (event.getState() != LoadDataEvent.State.POST_LOAD) {
                return;
            }

            final boolean shouldBeRegistered = npcFactory.createdAnIdentifier();
            if (shouldBeRegistered) {
                if (!registered) {
                    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                    registered = true;
                }
            } else if (registered) {
                HandlerList.unregisterAll(listener);
                registered = false;
            }
        }
    }
}
