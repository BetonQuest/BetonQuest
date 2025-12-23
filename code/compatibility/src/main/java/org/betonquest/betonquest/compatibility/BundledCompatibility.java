package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.auraskills.AuraSkillsIntegratorFactory;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegratorFactory;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegratorFactory;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.fabled.FabledIntegratorFactory;
import org.betonquest.betonquest.compatibility.fakeblock.FakeBlockIntegratorFactory;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegratorFactory;
import org.betonquest.betonquest.compatibility.holograms.decentholograms.DecentHologramsIntegratorFactory;
import org.betonquest.betonquest.compatibility.holograms.holographicdisplays.HolographicDisplaysIntegratorFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobsRebornIntegratorFactory;
import org.betonquest.betonquest.compatibility.luckperms.LuckPermsIntegratorFactory;
import org.betonquest.betonquest.compatibility.magic.MagicIntegratorFactory;
import org.betonquest.betonquest.compatibility.mcmmo.McMMOIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOCoreIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmolib.MythicLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegratorFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensIntegratorFactory;
import org.betonquest.betonquest.compatibility.npc.fancynpcs.FancyNpcsIntegrator;
import org.betonquest.betonquest.compatibility.npc.fancynpcs.FancyNpcsIntegratorFactory;
import org.betonquest.betonquest.compatibility.npc.znpcsplus.ZNPCsPlusIntegrator;
import org.betonquest.betonquest.compatibility.npc.znpcsplus.ZNPCsPlusIntegratorFactory;
import org.betonquest.betonquest.compatibility.packetevents.PacketEventsIntegratorFactory;
import org.betonquest.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegratorFactory;
import org.betonquest.betonquest.compatibility.quests.QuestsIntegratorFactory;
import org.betonquest.betonquest.compatibility.redischat.RedisChatIntegratorFactory;
import org.betonquest.betonquest.compatibility.shopkeepers.ShopkeepersIntegratorFactory;
import org.betonquest.betonquest.compatibility.skript.SkriptIntegratorFactory;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsIntegratorFactory;
import org.betonquest.betonquest.compatibility.vault.VaultIntegratorFactory;
import org.betonquest.betonquest.compatibility.worldedit.WorldEditIntegratorFactory;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Allows to register the 3rd party compatibility.
 */
public class BundledCompatibility {

    /**
     * Compatibility to register at.
     */
    private final Compatibility compatibility;

    /**
     * API used for registering.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * Plugin to start tasks and register listener.
     */
    private final Plugin plugin;

    /**
     * Creates a new Object to register plugin compatibilities.
     *
     * @param compatibility the compatibility to register at
     * @param betonQuestApi the API used for registering
     * @param plugin        the plugin to start tasks and register listener
     */
    public BundledCompatibility(final Compatibility compatibility, final BetonQuestApi betonQuestApi, final Plugin plugin) {
        this.compatibility = compatibility;
        this.betonQuestApi = betonQuestApi;
        this.plugin = plugin;
    }

    /**
     * Registers the Factories.
     */
    public void registerCompatiblePlugins() {
        final BetonQuestLoggerFactory loggerFactory = betonQuestApi.getLoggerFactory();
        compatibility.register("MythicMobs", new MythicMobsIntegratorFactory());
        compatibility.register("Citizens", new CitizensIntegratorFactory());
        compatibility.register("Vault", new VaultIntegratorFactory());
        compatibility.register("Skript", new SkriptIntegratorFactory());
        compatibility.register("WorldGuard", new WorldGuardIntegratorFactory());
        compatibility.register("WorldEdit", new WorldEditIntegratorFactory());
        compatibility.register("FastAsyncWorldEdit", new WorldEditIntegratorFactory());
        compatibility.register("mcMMO", new McMMOIntegratorFactory(plugin));
        compatibility.register("MythicLib", new MythicLibIntegratorFactory());
        compatibility.register("MMOCore", new MMOCoreIntegratorFactory());
        compatibility.register("MMOItems", new MMOItemsIntegratorFactory(plugin));
        compatibility.register("EffectLib", new EffectLibIntegratorFactory());
        compatibility.register("Heroes", new HeroesIntegratorFactory(plugin));
        compatibility.register("Magic", new MagicIntegratorFactory(plugin));
        compatibility.register("Denizen", new DenizenIntegratorFactory());
        compatibility.register("Fabled", new FabledIntegratorFactory(plugin));
        compatibility.register("Quests", new QuestsIntegratorFactory());
        compatibility.register("Shopkeepers", new ShopkeepersIntegratorFactory());
        compatibility.register("PlaceholderAPI", new PlaceholderAPIIntegratorFactory(plugin.getDescription()));
        compatibility.register("packetevents", new PacketEventsIntegratorFactory());
        compatibility.register("Brewery", new BreweryIntegratorFactory());
        compatibility.register("BreweryX", new BreweryIntegratorFactory());
        compatibility.register("Jobs", new JobsRebornIntegratorFactory());
        compatibility.register("LuckPerms", new LuckPermsIntegratorFactory());
        compatibility.register("AuraSkills", new AuraSkillsIntegratorFactory());
        compatibility.register("DecentHolograms", new DecentHologramsIntegratorFactory(loggerFactory,
                betonQuestApi.getQuestTypeApi().variables(), betonQuestApi.getQuestPackageManager()));
        compatibility.register("HolographicDisplays", new HolographicDisplaysIntegratorFactory(loggerFactory,
                betonQuestApi.getQuestPackageManager()));
        compatibility.register("fake-block", new FakeBlockIntegratorFactory(plugin));
        compatibility.register("RedisChat", new RedisChatIntegratorFactory());
        compatibility.register("Train_Carts", new TrainCartsIntegratorFactory());
        compatibility.register(FancyNpcsIntegrator.PREFIX, new FancyNpcsIntegratorFactory(plugin));
        compatibility.register(ZNPCsPlusIntegrator.PREFIX, new ZNPCsPlusIntegratorFactory(plugin));
    }
}
