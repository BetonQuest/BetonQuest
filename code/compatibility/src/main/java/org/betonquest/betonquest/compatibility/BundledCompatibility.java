package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.auraskills.AuraSkillsIntegrator;
import org.betonquest.betonquest.compatibility.brewery.BreweryIntegrator;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineIntegrator;
import org.betonquest.betonquest.compatibility.denizen.DenizenIntegrator;
import org.betonquest.betonquest.compatibility.effectlib.EffectLibIntegrator;
import org.betonquest.betonquest.compatibility.fabled.FabledIntegrator;
import org.betonquest.betonquest.compatibility.fakeblock.FakeBlockIntegrator;
import org.betonquest.betonquest.compatibility.heroes.HeroesIntegrator;
import org.betonquest.betonquest.compatibility.holograms.decentholograms.DecentHologramsIntegrator;
import org.betonquest.betonquest.compatibility.holograms.fancyholograms.FancyHologramsIntegrator;
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
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;

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

    private boolean shouldNotRegister(final String name) {
        final boolean isEnabled = config.getBoolean("hook." + name.toLowerCase(Locale.ROOT));
        if (!isEnabled) {
            log.debug("Did not register hook %s because it is disabled".formatted(name));
            return true;
        }
        return false;
    }

    private void register(final String name, final Supplier<Integration> integrationSupplier, final String versionString) {
        if (shouldNotRegister(name)) {
            return;
        }
        integrationService
                .withPolicies(Policies.minimalPluginVersion(name,
                        VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, versionString)))
                .register(plugin, integrationSupplier);
    }

    private void register(final String name, final Supplier<Integration> integrationSupplier, final Policy... polices) {
        if (shouldNotRegister(name)) {
            return;
        }
        integrationService
                .withPolicies(Policies.requirePlugin(name))
                .withPolicies(polices)
                .register(plugin, integrationSupplier);
    }

    /**
     * Registers the compatible and enabled integrations.
     *
     * @param servicesManager     the Bukkit services manager
     * @param processorDataLoader the processor data loader to use
     */
    @SuppressWarnings("Convert2MethodRef") //ClassNotFoundException on load up if certain integrations are absent
    public void registerCompatiblePlugins(final ServicesManager servicesManager, final ProcessorDataLoader processorDataLoader) {
        register("MythicMobs", () -> new MythicMobsIntegrator(plugin, config), MythicMobsIntegrator.REQUIRED_VERSION);
        register("Citizens", () -> new CitizensIntegrator());
        register("Vault", () -> new VaultIntegrator(servicesManager));
        register("Skript", () -> new SkriptIntegrator());
        register("WorldGuard", () -> new WorldGuardIntegrator());
        register("WorldEdit", () -> new WorldEditIntegrator());
        register("FastAsyncWorldEdit", () -> new WorldEditIntegrator());
        register("mcMMO", () -> new McMMOIntegrator());
        register("MythicLib", () -> new MythicLibIntegrator());
        register("MMOCore", () -> new MMOCoreIntegrator());
        register("MMOItems", () -> new MMOItemsIntegrator());
        register("EffectLib", () -> new EffectLibIntegrator(plugin, processorDataLoader));
        register("Heroes", () -> new HeroesIntegrator());
        register("Magic", () -> new MagicIntegrator());
        register("Denizen", () -> new DenizenIntegrator());
        register("Fabled", () -> new FabledIntegrator());
        register("Quests", () -> new QuestsIntegrator(), QuestsIntegrator.classPolicy());
        register("Shopkeepers", () -> new ShopkeepersIntegrator(), ShopkeepersIntegrator.REQUIRED_VERSION);
        register("PlaceholderAPI", () -> new PlaceholderAPIIntegrator(plugin.getDescription()));
        register("packetevents", () -> new PacketEventsIntegrator(), PacketEventsIntegrator.REQUIRED_VERSION);
        register("Brewery", () -> new BreweryIntegrator());
        register("BreweryX", () -> new BreweryIntegrator());
        register("Jobs", () -> new JobsRebornIntegrator());
        register("LuckPerms", () -> new LuckPermsIntegrator(servicesManager));
        register("AuraSkills", () -> new AuraSkillsIntegrator());
        register("DecentHolograms", () -> new DecentHologramsIntegrator(),
                DecentHologramsIntegrator.REQUIRED_VERSION);
        register("HolographicDisplays", () -> new HolographicDisplaysIntegrator(plugin),
                HolographicDisplaysIntegrator.REQUIRED_VERSION);
        register("fake-block", () -> new FakeBlockIntegrator(servicesManager), FakeBlockIntegrator.REQUIRED_VERSION);
        register("RedisChat", () -> new RedisChatIntegrator());
        register("Train_Carts", () -> new TrainCartsIntegrator());
        register(FancyNpcsIntegrator.PREFIX, () -> new FancyNpcsIntegrator(plugin));
        register(FancyHologramsIntegrator.NAME, () -> new FancyHologramsIntegrator(), FancyHologramsIntegrator.getPolicies());
        register(ZNPCsPlusIntegrator.PREFIX, () -> new ZNPCsPlusIntegrator(), ZNPCsPlusIntegrator.REQUIRED_VERSION);
        register("Nexo", () -> new NexoIntegrator());
        register("CraftEngine", () -> new CraftEngineIntegrator());
        register("ItemsAdder", () -> new ItemsAdderIntegrator(), ItemsAdderIntegrator.REQUIRED_VERSION);
    }
}
