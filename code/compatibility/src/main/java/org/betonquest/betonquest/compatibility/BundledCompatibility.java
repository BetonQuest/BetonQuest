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
        compatibility.registerPlugin("MythicMobs", new MythicMobsIntegratorFactory());
        compatibility.registerPlugin("Citizens", new CitizensIntegratorFactory());
        compatibility.registerPlugin("Vault", new VaultIntegratorFactory());
        compatibility.registerPlugin("Skript", new SkriptIntegratorFactory());
        compatibility.registerPlugin("WorldGuard", new WorldGuardIntegratorFactory());
        compatibility.registerPlugin("WorldEdit", new WorldEditIntegratorFactory());
        compatibility.registerPlugin("FastAsyncWorldEdit", new WorldEditIntegratorFactory());
        compatibility.registerPlugin("mcMMO", new McMMOIntegratorFactory(plugin));
        compatibility.registerPlugin("MythicLib", new MythicLibIntegratorFactory());
        compatibility.registerPlugin("MMOCore", new MMOCoreIntegratorFactory());
        compatibility.registerPlugin("MMOItems", new MMOItemsIntegratorFactory(plugin));
        compatibility.registerPlugin("EffectLib", new EffectLibIntegratorFactory());
        compatibility.registerPlugin("Heroes", new HeroesIntegratorFactory(plugin));
        compatibility.registerPlugin("Magic", new MagicIntegratorFactory(plugin));
        compatibility.registerPlugin("Denizen", new DenizenIntegratorFactory());
        compatibility.registerPlugin("Fabled", new FabledIntegratorFactory(plugin));
        compatibility.registerPlugin("Quests", new QuestsIntegratorFactory());
        compatibility.registerPlugin("Shopkeepers", new ShopkeepersIntegratorFactory());
        compatibility.registerPlugin("PlaceholderAPI", new PlaceholderAPIIntegratorFactory(plugin.getDescription()));
        compatibility.registerPlugin("packetevents", new PacketEventsIntegratorFactory());
        compatibility.registerPlugin("Brewery", new BreweryIntegratorFactory());
        compatibility.registerPlugin("BreweryX", new BreweryIntegratorFactory());
        compatibility.registerPlugin("Jobs", new JobsRebornIntegratorFactory());
        compatibility.registerPlugin("LuckPerms", new LuckPermsIntegratorFactory());
        compatibility.registerPlugin("AuraSkills", new AuraSkillsIntegratorFactory());
        compatibility.registerPlugin("DecentHolograms", new DecentHologramsIntegratorFactory(loggerFactory,
                betonQuestApi.getQuestTypeApi().placeholders(), betonQuestApi.getQuestPackageManager()));
        compatibility.registerPlugin("HolographicDisplays", new HolographicDisplaysIntegratorFactory(loggerFactory,
                betonQuestApi.getQuestPackageManager()));
        compatibility.registerPlugin("fake-block", new FakeBlockIntegratorFactory(plugin));
        compatibility.registerPlugin("RedisChat", new RedisChatIntegratorFactory());
        compatibility.registerPlugin("Train_Carts", new TrainCartsIntegratorFactory());
        compatibility.registerPlugin(FancyNpcsIntegrator.PREFIX, new FancyNpcsIntegratorFactory(plugin));
        compatibility.registerPlugin(ZNPCsPlusIntegrator.PREFIX, new ZNPCsPlusIntegratorFactory(plugin));
    }
}
