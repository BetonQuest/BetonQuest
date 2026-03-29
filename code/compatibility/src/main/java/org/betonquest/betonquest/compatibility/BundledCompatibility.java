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
    @SuppressWarnings("Convert2MethodRef") //ClassNotFoundException on load up if certain integrations are absent
    public void registerCompatiblePlugins(final BetonQuestLoggerFactory loggerFactory, final ServicesManager servicesManager,
                                          final Instructions instructions, final Identifiers identifiers,
                                          final PlaceholderProcessor placeholderProcessor) {
        register("MythicMobs", MythicMobsIntegrator.REQUIRED_VERSION, () -> new MythicMobsIntegrator());
        register("Citizens", null, () -> new CitizensIntegrator());
        register("Vault", null, () -> new VaultIntegrator(servicesManager));
        register("Skript", null, () -> new SkriptIntegrator());
        register("WorldGuard", null, () -> new WorldGuardIntegrator());
        register("WorldEdit", null, () -> new WorldEditIntegrator());
        register("FastAsyncWorldEdit", null, () -> new WorldEditIntegrator());
        register("mcMMO", null, () -> new McMMOIntegrator());
        register("MythicLib", null, () -> new MythicLibIntegrator());
        register("MMOCore", null, () -> new MMOCoreIntegrator());
        register("MMOItems", null, () -> new MMOItemsIntegrator());
        register("EffectLib", null, () -> new EffectLibIntegrator());
        register("Heroes", null, () -> new HeroesIntegrator());
        register("Magic", null, () -> new MagicIntegrator());
        register("Denizen", null, () -> new DenizenIntegrator());
        register("Fabled", null, () -> new FabledIntegrator());
        register("Quests", null, () -> new QuestsIntegrator());
        register("Shopkeepers", ShopkeepersIntegrator.REQUIRED_VERSION, () -> new ShopkeepersIntegrator());
        register("PlaceholderAPI", null, () -> new PlaceholderAPIIntegrator(plugin.getDescription()));
        register("packetevents", PacketEventsIntegrator.REQUIRED_VERSION, () -> new PacketEventsIntegrator());
        register("Brewery", null, () -> new BreweryIntegrator());
        register("BreweryX", null, () -> new BreweryIntegrator());
        register("Jobs", null, () -> new JobsRebornIntegrator());
        register("LuckPerms", null, () -> new LuckPermsIntegrator(servicesManager));
        register("AuraSkills", null, () -> new AuraSkillsIntegrator());
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
        register("RedisChat", null, () -> new RedisChatIntegrator());
        register("Train_Carts", null, () -> new TrainCartsIntegrator());
        register(FancyNpcsIntegrator.PREFIX, null, () -> new FancyNpcsIntegrator());
        register(ZNPCsPlusIntegrator.PREFIX, ZNPCsPlusIntegrator.REQUIRED_VERSION, () -> new ZNPCsPlusIntegrator());
        register("Nexo", null, () -> new NexoIntegrator());
        register("CraftEngine", null, () -> new CraftEngineIntegrator());
        register("ItemsAdder", ItemsAdderIntegrator.REQUIRED_VERSION, () -> new ItemsAdderIntegrator());
    }
}
