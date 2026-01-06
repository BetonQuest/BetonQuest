package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
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
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
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
        mythicHider = new MythicHider(api.getProfileProvider(), plugin);
        mythicHider.reload(plugin.getPluginConfig().getInt("hider.npc_update_interval", 5 * 20));
        manager.registerEvents(mythicHider, plugin);

        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        questRegistries.condition().register("mythicmobdistance", new MythicMobDistanceConditionFactory(loggerFactory, mobExecutor, new MythicMobParser(mobExecutor)));
        questRegistries.objective().register("mmobkill", new MythicMobKillObjectiveFactory());
        questRegistries.action().registerCombined("mspawnmob", new MythicSpawnMobActionFactory(loggerFactory,
                new MythicMobDoubleParser(mobExecutor), plugin, mythicHider));
        questRegistries.action().register("mcast", new MythicCastSkillActionFactory(loggerFactory, apiHelper));

        final NpcRegistry npcRegistry = api.getFeatureRegistries().npc();
        manager.registerEvents(new MythicMobsInteractCatcher(api.getProfileProvider(), npcRegistry, mobExecutor, mythicHider), plugin);
        npcRegistry.register("mythicmobs", new MythicMobsNpcFactory(mobExecutor, mythicHider));
        npcRegistry.registerIdentifier(new MythicMobsReverseIdentifier());

        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
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
}
