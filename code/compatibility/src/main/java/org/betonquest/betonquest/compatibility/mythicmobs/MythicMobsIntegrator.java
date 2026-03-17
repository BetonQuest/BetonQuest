package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.BukkitManager;
import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.mythicmobs.action.MythicCastSkillActionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.action.MythicSpawnMobActionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.condition.MythicMobDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.item.MythicItemFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.item.MythicQuestItemSerializer;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsInteractCatcher;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsReverseIdentifier;
import org.betonquest.betonquest.compatibility.mythicmobs.objective.MythicMobKillObjectiveFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for MythicMobs.
 */
public class MythicMobsIntegrator implements Integration {

    /**
     * The minimum required version of MythicMobs.
     */
    public static final String REQUIRED_VERSION = "5.0.0";

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
    public void enable(final BetonQuestApi api) {
        final MythicBukkit mythicBukkit = MythicBukkit.inst();
        final BukkitAPIHelper apiHelper = mythicBukkit.getAPIHelper();
        final MobExecutor mobExecutor = mythicBukkit.getMobManager();

        final BukkitManager bukkitManager = api.bukkit();
        mythicHider = new MythicHider(api.profiles(), plugin);
        mythicHider.reload(plugin.getPluginConfig().getInt("hider.npc_update_interval", 5 * 20));
        bukkitManager.registerEvents(mythicHider);

        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        api.conditions().registry().register("mythicmobdistance", new MythicMobDistanceConditionFactory(mobExecutor, new MythicMobParser(mobExecutor)));
        api.objectives().registry().register("mmobkill", new MythicMobKillObjectiveFactory());
        api.actions().registry().registerCombined("mspawnmob", new MythicSpawnMobActionFactory(
                new MythicMobDoubleParser(mobExecutor), plugin, mythicHider));
        api.actions().registry().register("mcast", new MythicCastSkillActionFactory(loggerFactory, apiHelper));

        final NpcRegistry npcRegistry = api.npcs().registry();
        final Listener interactCatcher = new MythicMobsInteractCatcher(api.profiles(), npcRegistry, mobExecutor, mythicHider);
        final MythicMobsNpcFactory npcFactory = new MythicMobsNpcFactory(mobExecutor, mythicHider);
        bukkitManager.registerEvents(new DynamicListenerRegister(bukkitManager, interactCatcher, npcFactory));
        npcRegistry.register("mythicmobs", npcFactory);
        npcRegistry.registerIdentifier(new MythicMobsReverseIdentifier());

        final ItemRegistry itemRegistry = api.items().registry();
        final ItemExecutor itemManager = mythicBukkit.getItemManager();
        itemRegistry.register("mythic", new MythicItemFactory(itemManager));
        itemRegistry.registerSerializer("mythic", new MythicQuestItemSerializer(itemManager));
        api.reloader().register(ReloadPhase.INTEGRATION, this::reload);
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) throws QuestException {
        // Empty
    }

    private void reload() {
        if (mythicHider != null) {
            mythicHider.reload(plugin.getPluginConfig().getInt("hider.npc_update_interval", 5 * 20));
        }
    }

    @Override
    public void disable() {
        if (mythicHider != null) {
            mythicHider.stop();
            HandlerList.unregisterAll(mythicHider);
        }
    }

    /**
     * Handles de-/registration of a listener based on a condition which may change on reload.
     */
    public static final class DynamicListenerRegister implements Listener {

        /**
         * Bukkit manager to register the listener.
         */
        private final BukkitManager bukkitManager;

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

        private DynamicListenerRegister(final BukkitManager bukkitManager, final Listener listener, final MythicMobsNpcFactory npcFactory) {
            this.bukkitManager = bukkitManager;
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
                    bukkitManager.registerEvents(listener);
                    registered = true;
                }
            } else if (registered) {
                HandlerList.unregisterAll(listener);
                registered = false;
            }
        }
    }
}
