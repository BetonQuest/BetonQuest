package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.auraskills.AuraSkillsIntegrator;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegrator;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineIntegrator;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegrator;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegrator;
import org.betonquest.betonquest.compatibility.fabled.FabledIntegrator;
import org.betonquest.betonquest.compatibility.fakeblock.FakeBlockIntegrator;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegrator;
import org.betonquest.betonquest.compatibility.holograms.decentholograms.DecentHologramsIntegrator;
import org.betonquest.betonquest.compatibility.holograms.holographicdisplays.HolographicDisplaysIntegrator;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderIntegrator;
import org.betonquest.betonquest.compatibility.jobsreborn.JobsRebornIntegrator;
import org.betonquest.betonquest.compatibility.luckperms.LuckPermsIntegrator;
import org.betonquest.betonquest.compatibility.magic.MagicIntegrator;
import org.betonquest.betonquest.compatibility.mcmmo.McMMOIntegrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOCoreIntegrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsIntegrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmolib.MythicLibIntegrator;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobsIntegrator;
import org.betonquest.betonquest.compatibility.nexo.NexoIntegrator;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensIntegrator;
import org.betonquest.betonquest.compatibility.npc.fancynpcs.FancyNpcsIntegrator;
import org.betonquest.betonquest.compatibility.npc.znpcsplus.ZNPCsPlusIntegrator;
import org.betonquest.betonquest.compatibility.packetevents.PacketEventsIntegrator;
import org.betonquest.betonquest.compatibility.placeholderapi.PlaceholderAPIIntegrator;
import org.betonquest.betonquest.compatibility.quests.QuestsIntegrator;
import org.betonquest.betonquest.compatibility.redischat.RedisChatIntegrator;
import org.betonquest.betonquest.compatibility.shopkeepers.ShopkeepersIntegrator;
import org.betonquest.betonquest.compatibility.skript.SkriptIntegrator;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsIntegrator;
import org.betonquest.betonquest.compatibility.vault.VaultIntegrator;
import org.betonquest.betonquest.compatibility.worldedit.WorldEditIntegrator;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Allows registering the 3rd party compatibility provided by BetonQuest.
 */
public final class BundledCompatibility {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Config for checking if an Integrator should be registered.
     */
    private final ConfigAccessor config;

    /**
     * Source plugin instance.
     */
    private final Plugin plugin;

    /**
     * Service instance to register the integrations to.
     */
    private final IntegrationService integrationService;

    /**
     * Creates a new Bundled Compatibility instance to register enabled integrations.
     *
     * @param log                the custom logger for this class
     * @param config             the config to check if an Integrator should be activated/hooked
     * @param plugin             the plugin instance
     * @param integrationService the service instance to register the integrations to
     */
    public BundledCompatibility(final BetonQuestLogger log, final ConfigAccessor config, final Plugin plugin,
                                final IntegrationService integrationService) {
        this.log = log;
        this.config = config;
        this.plugin = plugin;
        this.integrationService = integrationService;
    }

    private void register(final String name, @Nullable final String version,
                          final Supplier<Integration> integrationSupplier) {
        final boolean isEnabled = config.getBoolean("hook." + name.toLowerCase(Locale.ROOT));
        if (!isEnabled) {
            log.debug("Did not register hook %s because it is disabled".formatted(name));
            return;
        }
        final Policy policy = version == null ? Policies.requirePlugin(name) : Policies.minimalPluginVersion(name, version);
        integrationService.withPolicies(policy).register(plugin, integrationSupplier);
    }

    /**
     * Registers the compatible and enabled integrations.
     *
     * @param servicesManager      the Bukkit services manager
     * @param loggerFactory        the logger factory to use
     * @param instructions         the instructions instance
     * @param identifiers          the identifiers instance
     * @param placeholderProcessor the placeholder processor to use
     */
    public void registerCompatiblePlugins(final BetonQuestLoggerFactory loggerFactory, final ServicesManager servicesManager,
                                          final Instructions instructions, final Identifiers identifiers,
                                          final PlaceholderProcessor placeholderProcessor) {
        //noinspection Convert2MethodRef - will cause ClassNotFoundException on startup.
        register("MythicMobs", MythicMobsIntegrator.REQUIRED_VERSION, () -> new MythicMobsIntegrator());
        register("Citizens", null, CitizensIntegrator::new);
        register("Vault", null, () -> new VaultIntegrator(servicesManager));
        register("Skript", null, SkriptIntegrator::new);
        register("WorldGuard", null, WorldGuardIntegrator::new);
        register("WorldEdit", null, WorldEditIntegrator::new);
        register("FastAsyncWorldEdit", null, WorldEditIntegrator::new);
        register("mcMMO", null, McMMOIntegrator::new);
        register("MythicLib", null, MythicLibIntegrator::new);
        register("MMOCore", null, MMOCoreIntegrator::new);
        register("MMOItems", null, MMOItemsIntegrator::new);
        register("EffectLib", null, EffectLibIntegrator::new);
        register("Heroes", null, HeroesIntegrator::new);
        register("Magic", null, MagicIntegrator::new);
        register("Denizen", null, DenizenIntegrator::new);
        register("Fabled", null, FabledIntegrator::new);
        register("Quests", null, QuestsIntegrator::new);
        register("Shopkeepers", ShopkeepersIntegrator.REQUIRED_VERSION, ShopkeepersIntegrator::new);
        register("PlaceholderAPI", null, () -> new PlaceholderAPIIntegrator(plugin.getDescription()));
        register("packetevents", PacketEventsIntegrator.REQUIRED_VERSION, PacketEventsIntegrator::new);
        register("Brewery", null, BreweryIntegrator::new);
        register("BreweryX", null, BreweryIntegrator::new);
        register("Jobs", null, JobsRebornIntegrator::new);
        register("LuckPerms", null, () -> new LuckPermsIntegrator(servicesManager));
        register("AuraSkills", null, AuraSkillsIntegrator::new);
        try {
            final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory =
                    identifiers.getFactory(PlaceholderIdentifier.class);
            register("DecentHolograms", DecentHologramsIntegrator.REQUIRED_VERSION,
                    () -> new DecentHologramsIntegrator(loggerFactory.create(DecentHologramsIntegrator.class),
                            placeholderIdentifierFactory, instructions));
            register("HolographicDisplays", HolographicDisplaysIntegrator.REQUIRED_VERSION,
                    () -> new HolographicDisplaysIntegrator(loggerFactory.create(HolographicDisplaysIntegrator.class),
                            instructions, placeholderIdentifierFactory, placeholderProcessor));
        } catch (final QuestException e) {
            log.warn("Could not register DecentHolograms and HolographicDisplays compatibility.", e);
        }
        register("fake-block", FakeBlockIntegrator.REQUIRED_VERSION, () -> new FakeBlockIntegrator(servicesManager));
        register("RedisChat", null, RedisChatIntegrator::new);
        register("Train_Carts", null, TrainCartsIntegrator::new);
        register(FancyNpcsIntegrator.PREFIX, null, FancyNpcsIntegrator::new);
        register(ZNPCsPlusIntegrator.PREFIX, ZNPCsPlusIntegrator.REQUIRED_VERSION, ZNPCsPlusIntegrator::new);
        register("Nexo", null, NexoIntegrator::new);
        register("CraftEngine", null, CraftEngineIntegrator::new);
        register("ItemsAdder", ItemsAdderIntegrator.REQUIRED_VERSION, ItemsAdderIntegrator::new);
    }
}
