package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.auraskills.AuraSkillsIntegratorFactory;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegratorFactory;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineIntegratorFactory;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegratorFactory;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.fabled.FabledIntegratorFactory;
import org.betonquest.betonquest.compatibility.fakeblock.FakeBlockIntegratorFactory;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegratorFactory;
import org.betonquest.betonquest.compatibility.holograms.decentholograms.DecentHologramsIntegratorFactory;
import org.betonquest.betonquest.compatibility.holograms.holographicdisplays.HolographicDisplaysIntegratorFactory;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderIntegratorFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobsRebornIntegratorFactory;
import org.betonquest.betonquest.compatibility.luckperms.LuckPermsIntegratorFactory;
import org.betonquest.betonquest.compatibility.magic.MagicIntegratorFactory;
import org.betonquest.betonquest.compatibility.mcmmo.McMMOIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOCoreIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsIntegratorFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmolib.MythicLibIntegratorFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegratorFactory;
import org.betonquest.betonquest.compatibility.nexo.NexoIntegratorFactory;
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
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.bukkit.plugin.Plugin;

/**
 * Allows registering the 3rd party compatibility.
 */
public final class BundledCompatibility {

    /**
     * Default constructor.
     */
    private BundledCompatibility() {
    }

    /**
     * Registers the compatible factories.
     *
     * @param loggerFactory        the logger factory to use
     * @param logger               the logger to use
     * @param compatibility        the compatibility instance to register the factories to
     * @param instructions         the instructions instance
     * @param identifiers          the identifiers instance
     * @param globalData           the global data instance
     * @param placeholderProcessor the placeholder processor to use
     * @param plugin               the plugin instance
     */
    public static void registerCompatiblePlugins(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger logger,
                                                 final Compatibility compatibility, final Instructions instructions,
                                                 final Identifiers identifiers, final GlobalData globalData,
                                                 final PlaceholderProcessor placeholderProcessor, final Plugin plugin) {
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
        try {
            final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory =
                    identifiers.getFactory(PlaceholderIdentifier.class);
            compatibility.registerPlugin("DecentHolograms", new DecentHologramsIntegratorFactory(loggerFactory,
                    instructions, placeholderIdentifierFactory));
            compatibility.registerPlugin("HolographicDisplays", new HolographicDisplaysIntegratorFactory(loggerFactory,
                    instructions, placeholderIdentifierFactory, placeholderProcessor));
        } catch (final QuestException e) {
            logger.warn("Could not register DecentHolograms and HolographicDisplays compatibility.", e);
        }
        compatibility.registerPlugin("fake-block", new FakeBlockIntegratorFactory(plugin));
        compatibility.registerPlugin("RedisChat", new RedisChatIntegratorFactory());
        compatibility.registerPlugin("Train_Carts", new TrainCartsIntegratorFactory());
        compatibility.registerPlugin(FancyNpcsIntegrator.PREFIX, new FancyNpcsIntegratorFactory(plugin));
        compatibility.registerPlugin(ZNPCsPlusIntegrator.PREFIX, new ZNPCsPlusIntegratorFactory(plugin));
        compatibility.registerPlugin("Nexo", new NexoIntegratorFactory());
        compatibility.registerPlugin("CraftEngine", new CraftEngineIntegratorFactory());
        compatibility.registerPlugin("ItemsAdder", new ItemsAdderIntegratorFactory());
    }
}
