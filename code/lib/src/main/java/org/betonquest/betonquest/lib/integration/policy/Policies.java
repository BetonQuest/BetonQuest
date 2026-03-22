package org.betonquest.betonquest.lib.integration.policy;

import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.bukkit.plugin.Plugin;

import java.util.function.Supplier;

/**
 * Utility class for creating and managing various policies related to versioning and plugin dependencies.
 * This class provides a set of static factory methods to define conditions, version requirements, and
 * plugin requirements. All policies created by this class ensure compliance with predefined rules and
 * constraints applicable to plugins and Minecraft versions.
 * <p>
 * This class is non-instantiable and serves only as a utility.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Policies {

    /**
     * The name of an unknown plugin.
     */
    private static final String UNKNOWN_PLUGIN_NAME = "unknown";

    /**
     * Not supposed to be instantiated.
     */
    private Policies() {
    }

    /**
     * Creates a policy based on a custom condition with a default message.
     *
     * @param condition the condition supplier that returns true if the policy is met
     * @return a policy that checks the given condition
     */
    public static Policy simpleCondition(final Supplier<Boolean> condition) {
        return simpleCondition(condition, "A user-defined condition has to be met.");
    }

    /**
     * Creates a policy based on a custom condition with a given message.
     *
     * @param condition the condition supplier that returns true if the policy is met
     * @param message   the message to display when the policy is not met
     * @return a policy that checks the given condition
     */
    public static Policy simpleCondition(final Supplier<Boolean> condition, final String message) {
        return new SimpleConditionPolicy(condition, message);
    }

    /**
     * Creates a policy requiring a minimum Minecraft version.
     *
     * @param version the minimum Minecraft version required
     * @return a policy that checks for the minimum Minecraft version
     */
    public static Policy minimalVanillaVersion(final String version) {
        return new VanillaPolicy(version, VersionCompareStrategy.MINIMAL, "Minecraft version '%s' and above is required.".formatted(version));
    }

    /**
     * Creates a policy requiring an exact Minecraft version.
     *
     * @param version the exact Minecraft version required
     * @return a policy that checks for the exact Minecraft version
     */
    public static Policy exactVanillaVersion(final String version) {
        return new VanillaPolicy(version, VersionCompareStrategy.EXACT, "Minecraft version '%s' is required.".formatted(version));
    }

    /**
     * Creates a policy requiring a maximum Minecraft version.
     *
     * @param version the maximum Minecraft version allowed
     * @return a policy that checks for the maximum Minecraft version
     */
    public static Policy maximalVanillaVersion(final String version) {
        return new VanillaPolicy(version, VersionCompareStrategy.MAXIMAL, "Minecraft version '%s' or below is required.".formatted(version));
    }

    /**
     * Creates a policy requiring a plugin by name.
     *
     * @param plugin the name of the required plugin
     * @return a policy that checks for the plugin's presence
     */
    public static Policy requirePlugin(final String plugin) {
        return new UnversionedPluginPolicy(PluginProvider.forName(plugin), "Plugin '%s' is required.".formatted(plugin));
    }

    /**
     * Creates a policy requiring a plugin by main class.
     *
     * @param pluginClass the main class of the required plugin
     * @return a policy that checks for the plugin's presence
     */
    public static Policy requirePlugin(final Class<? extends Plugin> pluginClass) {
        return new UnversionedPluginPolicy(PluginProvider.forClass(pluginClass), "Plugin with main class '%s' is required.".formatted(pluginClass.getName()));
    }

    /**
     * Creates a policy requiring a plugin by instance.
     *
     * @param plugin the required plugin
     * @return a policy that checks for the plugin's presence
     */
    public static Policy requirePlugin(final Plugin plugin) {
        return new UnversionedPluginPolicy(PluginProvider.forInstance(plugin), "Plugin '%s' is required.".formatted(plugin.getName()));
    }

    /**
     * Creates a policy requiring a plugin by a {@link PluginProvider}.
     *
     * @param pluginProvider the provider for the required plugin
     * @return a policy that checks for the plugin's presence
     */
    public static Policy requirePlugin(final PluginProvider pluginProvider) {
        return new UnversionedPluginPolicy(pluginProvider, "Plugin '%s' is required.".formatted(pluginProvider.name().orElse(UNKNOWN_PLUGIN_NAME)));
    }

    /**
     * Creates a policy requiring a minimum plugin version by plugin name.
     *
     * @param plugin  the name of the plugin
     * @param version the minimum version required
     * @return a policy that checks for the minimum plugin version
     */
    public static Policy minimalPluginVersion(final String plugin, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forName(plugin), version, VersionCompareStrategy.MINIMAL,
                "Plugin '%s' version '%s' and above is required.".formatted(plugin, version));
    }

    /**
     * Creates a policy requiring a minimum plugin version by plugin class.
     *
     * @param pluginClass the main class of the plugin
     * @param version     the minimum version required
     * @return a policy that checks for the minimum plugin version
     */
    public static Policy minimalPluginVersion(final Class<? extends Plugin> pluginClass, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forClass(pluginClass), version, VersionCompareStrategy.MINIMAL,
                "Plugin with main class '%s' version '%s' and above is required.".formatted(pluginClass.getName(), version));
    }

    /**
     * Creates a policy requiring a minimum plugin version by plugin instance.
     *
     * @param plugin  the plugin instance
     * @param version the minimum version required
     * @return a policy that checks for the minimum plugin version
     */
    public static Policy minimalPluginVersion(final Plugin plugin, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forInstance(plugin), version, VersionCompareStrategy.MINIMAL,
                "Plugin '%s' version '%s' and above is required.".formatted(plugin.getName(), version));
    }

    /**
     * Creates a policy requiring a minimum plugin version by a {@link PluginProvider}.
     *
     * @param pluginProvider the provider for the plugin
     * @param version        the minimum version required
     * @return a policy that checks for the minimum plugin version
     */
    public static Policy minimalPluginVersion(final PluginProvider pluginProvider, final String version) {
        return new VersionedPluginPolicy(pluginProvider, version, VersionCompareStrategy.MINIMAL,
                "Plugin '%s' version '%s' and above is required.".formatted(pluginProvider.name().orElse(UNKNOWN_PLUGIN_NAME), version));
    }

    /**
     * Creates a policy requiring an exact plugin version by plugin name.
     *
     * @param plugin  the name of the plugin
     * @param version the exact version required
     * @return a policy that checks for the exact plugin version
     */
    public static Policy exactPluginVersion(final String plugin, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forName(plugin), version, VersionCompareStrategy.EXACT,
                "Plugin '%s' version '%s' is required.".formatted(plugin, version));
    }

    /**
     * Creates a policy requiring an exact plugin version by plugin class.
     *
     * @param pluginClass the main class of the plugin
     * @param version     the exact version required
     * @return a policy that checks for the exact plugin version
     */
    public static Policy exactPluginVersion(final Class<? extends Plugin> pluginClass, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forClass(pluginClass), version, VersionCompareStrategy.EXACT,
                "Plugin with main class '%s' version '%s' is required.".formatted(pluginClass.getName(), version));
    }

    /**
     * Creates a policy requiring an exact plugin version by plugin instance.
     *
     * @param plugin  the plugin instance
     * @param version the exact version required
     * @return a policy that checks for the exact plugin version
     */
    public static Policy exactPluginVersion(final Plugin plugin, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forInstance(plugin), version, VersionCompareStrategy.EXACT,
                "Plugin '%s' version '%s' is required.".formatted(plugin.getName(), version));
    }

    /**
     * Creates a policy requiring an exact plugin version by a {@link PluginProvider}.
     *
     * @param pluginProvider the provider for the plugin
     * @param version        the exact version required
     * @return a policy that checks for the exact plugin version
     */
    public static Policy exactPluginVersion(final PluginProvider pluginProvider, final String version) {
        return new VersionedPluginPolicy(pluginProvider, version, VersionCompareStrategy.EXACT,
                "Plugin '%s' version '%s' is required.".formatted(pluginProvider.name().orElse(UNKNOWN_PLUGIN_NAME), version));
    }

    /**
     * Creates a policy requiring a maximum plugin version by plugin name.
     *
     * @param plugin  the name of the plugin
     * @param version the maximum version allowed
     * @return a policy that checks for the maximum plugin version
     */
    public static Policy maximalPluginVersion(final String plugin, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forName(plugin), version, VersionCompareStrategy.MAXIMAL,
                "Plugin '%s' version '%s' and below is required.".formatted(plugin, version));
    }

    /**
     * Creates a policy requiring a maximum plugin version by plugin class.
     *
     * @param pluginClass the main class of the plugin
     * @param version     the maximum version allowed
     * @return a policy that checks for the maximum plugin version
     */
    public static Policy maximalPluginVersion(final Class<? extends Plugin> pluginClass, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forClass(pluginClass), version, VersionCompareStrategy.MAXIMAL,
                "Plugin with main class '%s' version '%s' and below is required.".formatted(pluginClass.getName(), version));
    }

    /**
     * Creates a policy requiring a maximum plugin version by plugin instance.
     *
     * @param plugin  the plugin instance
     * @param version the maximum version allowed
     * @return a policy that checks for the maximum plugin version
     */
    public static Policy maximalPluginVersion(final Plugin plugin, final String version) {
        return new VersionedPluginPolicy(PluginProvider.forInstance(plugin), version, VersionCompareStrategy.MAXIMAL,
                "Plugin '%s' version '%s' and below is required.".formatted(plugin.getName(), version));
    }

    /**
     * Creates a policy requiring a maximum plugin version by a {@link PluginProvider}.
     *
     * @param pluginProvider the provider for the plugin
     * @param version        the maximum version allowed
     * @return a policy that checks for the maximum plugin version
     */
    public static Policy maximalPluginVersion(final PluginProvider pluginProvider, final String version) {
        return new VersionedPluginPolicy(pluginProvider, version, VersionCompareStrategy.MAXIMAL,
                "Plugin '%s' version '%s' and below is required.".formatted(pluginProvider.name().orElse(UNKNOWN_PLUGIN_NAME), version));
    }

    /**
     * Creates policies for a Minecraft version range.
     *
     * @param minVersion the minimum Minecraft version
     * @param maxVersion the maximum Minecraft version
     * @return an array of policies that check for the version range
     */
    public static Policy[] vanillaVersionRange(final String minVersion, final String maxVersion) {
        return new Policy[]{
                minimalVanillaVersion(minVersion),
                maximalVanillaVersion(maxVersion)
        };
    }

    /**
     * Creates policies for a plugin version range by plugin name.
     *
     * @param plugin     the name of the plugin
     * @param minVersion the minimum plugin version
     * @param maxVersion the maximum plugin version
     * @return an array of policies that check for the version range
     */
    public static Policy[] pluginVersionRange(final String plugin, final String minVersion, final String maxVersion) {
        return new Policy[]{
                minimalPluginVersion(plugin, minVersion),
                maximalPluginVersion(plugin, maxVersion)
        };
    }

    /**
     * Creates policies for a plugin version range by plugin class.
     *
     * @param pluginClass the main class of the plugin
     * @param minVersion  the minimum plugin version
     * @param maxVersion  the maximum plugin version
     * @return an array of policies that check for the version range
     */
    public static Policy[] pluginVersionRange(final Class<? extends Plugin> pluginClass, final String minVersion, final String maxVersion) {
        return new Policy[]{
                minimalPluginVersion(pluginClass, minVersion),
                maximalPluginVersion(pluginClass, maxVersion)
        };
    }

    /**
     * Creates policies for a plugin version range by plugin instance.
     *
     * @param plugin     the plugin instance
     * @param minVersion the minimum plugin version
     * @param maxVersion the maximum plugin version
     * @return an array of policies that check for the version range
     */
    public static Policy[] pluginVersionRange(final Plugin plugin, final String minVersion, final String maxVersion) {
        return new Policy[]{
                minimalPluginVersion(plugin, minVersion),
                maximalPluginVersion(plugin, maxVersion)
        };
    }

    /**
     * Creates policies for a plugin version range by plugin provider.
     *
     * @param pluginProvider the provider for the plugin
     * @param minVersion     the minimum plugin version
     * @param maxVersion     the maximum plugin version
     * @return an array of policies that check for the version range
     */
    public static Policy[] pluginVersionRange(final PluginProvider pluginProvider, final String minVersion, final String maxVersion) {
        return new Policy[]{
                minimalPluginVersion(pluginProvider, minVersion),
                maximalPluginVersion(pluginProvider, maxVersion)
        };
    }
}
