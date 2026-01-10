package org.betonquest.betonquest.compatibility;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.versioning.MinecraftVersion;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads compatibility with other plugins.
 */
public class Compatibility {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest API.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * Config for checking if an Integrator should be activated.
     */
    private final ConfigAccessor config;

    /**
     * Version to use when a hook error message.
     */
    private final String version;

    /**
     * Integrations requiring a specific Minecraft version.
     * The key is the version string, the value data containing all integration factories and instances from it.
     */
    private final NavigableMap<Version, List<VanillaIntegrationData>> vanillaData = new TreeMap<>(new VersionComparator(UpdateStrategy.MAJOR));

    /**
     * A map of plugin names and their integration data.
     * The key is the name of the plugin, the value data containing all integration factories and instances from it.
     */
    private final Map<String, List<PluginIntegrationData>> pluginData = new TreeMap<>();

    /**
     * BetonQuest provided integrations.
     */
    private final BaseIntegrationSource betonQuestSource;

    /**
     * The instance of the HologramProvider.
     */
    @Nullable
    private HologramProvider hologramProvider;

    /**
     * Loads all compatibility with other plugins that is available in the current runtime.
     *
     * @param log           the custom logger for this class
     * @param config        the config to check if an Integrator should be activated/hooked
     * @param betonQuestApi the BetonQuest API used to hook plugins
     * @param version       the plugin version used in error messages
     */
    public Compatibility(final BetonQuestLogger log, final BetonQuestApi betonQuestApi, final ConfigAccessor config,
                         final String version) {
        this.log = log;
        this.betonQuestApi = betonQuestApi;
        this.config = config;
        this.version = version;
        this.betonQuestSource = new BaseIntegrationSource();
    }

    /**
     * Integrate plugins.
     */
    public void init() {
        vanillaData.headMap(new MinecraftVersion(), true).forEach((version, dataList) -> {
            log.info("Integrating into Minecraft " + version);
            dataList.forEach(VanillaIntegrationData::integrate);
        });
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            integratePlugin(plugin);
        }

        final String hooks = betonQuestSource.dataList.stream()
                .filter(IntegrationData::isIntegrated)
                .map(data -> data.getName() + " (" + data.getVersion() + ")")
                .collect(Collectors.joining(", "));
        if (!hooks.isEmpty()) {
            log.info("Enabled compatibility for " + hooks + "!");
        }
        postHook();
    }

    /**
     * Gets the list of hooked plugins in Alphabetical order.
     *
     * @return the list of hooked plugins
     */
    public List<String> getPluginNames() {
        return pluginData.entrySet().stream()
                .filter(entry -> entry
                        .getValue()
                        .stream()
                        .anyMatch(IntegrationData::isIntegrated))
                .map(Map.Entry::getKey).sorted().toList();
    }

    /**
     * Gets the BetonQuest integration source.
     *
     * @return BetonQuest provided integrations
     */
    public IntegrationSource getBetonQuestSource() {
        return betonQuestSource;
    }

    /**
     * Gets all existent integrators ordered in a single list.
     * <p>
     * First all Minecraft versions in ascending order.
     * Then the plugins in alphabetical order.
     *
     * @return flattened integrators
     */
    private Map<Integrator, BaseIntegrationData> getAllIntegrators() {
        return Stream.concat(vanillaData.values().stream(), pluginData.values().stream())
                .flatMap(Collection::stream)
                .filter(IntegrationData::isIntegrated)
                .collect(Collectors.toMap(data -> data.integrator,
                        data -> data, (a, b) -> {
                            throw new IllegalStateException("There shouldn't be equal Integrators");
                        }, LinkedHashMap::new));
    }

    /**
     * After all integrations are successfully hooked,
     * this method can be called to activate cross compatibility features.
     */
    private void postHook() {
        final List<HologramIntegrator> hologramIntegrators = new ArrayList<>();
        getAllIntegrators().forEach((integrator, data) -> {
            try {
                integrator.postHook();
                if (integrator instanceof final HologramIntegrator hologramIntegrator) {
                    hologramIntegrators.add(hologramIntegrator);
                }
            } catch (final HookException e) {
                log.warn("Error while enabling some features while post hooking into '" + data.name
                        + "': " + e.getMessage(), e);
            }
        });
        hologramProvider = new HologramProvider(hologramIntegrators);
        hologramProvider.hook(betonQuestApi);
    }

    /**
     * Reloads all loaded integrators.
     */
    public void reload() {
        getAllIntegrators().forEach((integrator, data) -> integrator.reload());
        if (hologramProvider != null) {
            hologramProvider.reload();
        }
    }

    /**
     * Disables all loaded integrators.
     */
    public void disable() {
        getAllIntegrators().forEach((integrator, data) -> integrator.close());
        if (hologramProvider != null) {
            hologramProvider.close();
        }
    }

    private void integratePlugin(final Plugin hookedPlugin) {
        if (!hookedPlugin.isEnabled()) {
            return;
        }
        final String name = hookedPlugin.getName();
        final List<PluginIntegrationData> list = pluginData.get(name);
        if (list == null || list.isEmpty()) {
            return;
        }

        final boolean isEnabled = config.getBoolean("hook." + name.toLowerCase(Locale.ROOT));
        if (!isEnabled) {
            log.debug("Did not hook " + name + " because it is disabled");
            return;
        }

        log.info("Hooking into " + name);
        list.forEach(data -> data.integratePlugin(hookedPlugin, name));
    }

    /**
     * Adds a new Integrator Factory for a Plugin with BetonQuest as source.
     *
     * @param name       the plugin name
     * @param integrator the integrator factory
     */
    public void registerPlugin(final String name, final IntegratorFactory integrator) {
        final PluginIntegrationData data = new PluginIntegrationData(name, integrator);
        betonQuestSource.dataList.add(data);
        pluginData.computeIfAbsent(name, ignored -> new ArrayList<>()).add(data);
    }

    /**
     * Adds a new Integrator Factory for a Minecraft version with BetonQuest as source.
     *
     * @param version    the version string
     * @param integrator the integrator factory
     */
    public void registerVanilla(final String version, final IntegratorFactory integrator) {
        final VanillaIntegrationData data = new VanillaIntegrationData(version, integrator);
        betonQuestSource.dataList.add(data);
        vanillaData.computeIfAbsent(new Version(version), ignored -> new ArrayList<>()).add(data);
    }

    /**
     * Holds integration factories and their created integrations from a single plugin.
     */
    private static final class BaseIntegrationSource implements IntegrationSource {

        /**
         * List of integration data for the plugin.
         */
        private final List<BaseIntegrationData> dataList = new ArrayList<>();

        private BaseIntegrationSource() {
        }

        @Override
        public List<IntegrationData> getDataList() {
            return List.copyOf(dataList);
        }
    }

    /**
     * Data for a specific integration of a plugin.
     */
    private abstract static class BaseIntegrationData implements IntegrationData {

        /**
         * The factory to create a new Integration.
         */
        protected final IntegratorFactory integratorFactory;

        /**
         * Name of integration target.
         */
        private final String name;

        /**
         * If an integration was attempted. The integrator may still be null if it was not successful.
         */
        protected boolean attempted;

        /**
         * The created Integrator.
         * The instance must only exist if hooked successfully.
         */
        @Nullable
        protected Integrator integrator;

        private BaseIntegrationData(final String name, final IntegratorFactory integratorFactory) {
            this.name = name;
            this.integratorFactory = integratorFactory;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isIntegrated() {
            return integrator != null;
        }
    }

    /**
     * Handles hooking into Plugins.
     */
    private final class PluginIntegrationData extends BaseIntegrationData {

        /**
         * The target plugin, if hooked.
         */
        @Nullable
        private Plugin target;

        private PluginIntegrationData(final String name, final IntegratorFactory integratorFactory) {
            super(name, integratorFactory);
        }

        @Override
        public String getVersion() {
            if (target == null) {
                throw new IllegalStateException("There is integrated plugin!");
            }
            return target.getDescription().getVersion();
        }

        private void integratePlugin(final Plugin hookedPlugin, final String name) {
            if (attempted) {
                return;
            }
            integrate(hookedPlugin, name);
            attempted = true;
        }

        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        private void integrate(final Plugin hookedPlugin, final String name) {
            try {
                final Integrator integrator = this.integratorFactory.getIntegrator();
                integrator.hook(betonQuestApi);
                this.integrator = integrator;
                this.target = hookedPlugin;
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
                final String message = String.format("There was an unexpected error while hooking into %s %s (BetonQuest %s, Server %s)! %s",
                        hookedPlugin.getName(),
                        hookedPlugin.getDescription().getVersion(),
                        version,
                        Bukkit.getVersion(),
                        exception.getMessage());
                log.error(message, exception);
                log.warn("BetonQuest will work correctly, except for that single integration. "
                        + "You can turn it off by setting 'hook." + name.toLowerCase(Locale.ROOT)
                        + "' to false in config.yml file.");
            }
        }
    }

    /**
     * Handles hooking into Minecraft versions.
     */
    private final class VanillaIntegrationData extends BaseIntegrationData {

        /**
         * The minecraft version string.
         */
        private final String version;

        private VanillaIntegrationData(final String version, final IntegratorFactory integratorFactory) {
            super("Minecraft", integratorFactory);
            this.version = version;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        private void integrate() {
            if (attempted) {
                return;
            }
            try {
                final Integrator integrator = this.integratorFactory.getIntegrator();
                integrator.hook(betonQuestApi);
                this.integrator = integrator;
            } catch (final HookException | RuntimeException exception) {
                log.warn("Could not hook into Minecraft %s! %s".formatted(version, exception.getMessage()), exception);
                log.warn("BetonQuest will work correctly, except for that single version integration.");
            }
            this.attempted = true;
        }
    }
}
