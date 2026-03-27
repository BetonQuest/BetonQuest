package org.betonquest.betonquest.compatibility;

import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.integration.IntegrationManager;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads compatibility with hologram plugins and provides access to integration readability.
 */
public class Compatibility implements Listener {

    /**
     * BetonQuest plugin name.
     */
    private static final String BETONQUEST = "BetonQuest";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest API.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * Manager for loaded integrations.
     */
    private final IntegrationManager integrationManager;

    /**
     * The instance of the HologramProvider.
     */
    @Nullable
    private HologramProvider hologramProvider;

    /**
     * Loads all compatibility with other plugins that is available in the current runtime.
     *
     * @param log                the custom logger for this class
     * @param betonQuestApi      the BetonQuest API used to hook plugins
     * @param integrationManager the manager for loaded integrations
     */
    public Compatibility(final BetonQuestLogger log, final BetonQuestApi betonQuestApi,
                         final IntegrationManager integrationManager) {
        this.log = log;
        this.betonQuestApi = betonQuestApi;
        this.integrationManager = integrationManager;
    }

    /**
     * Gets the list of hooked plugins in Alphabetical order.
     *
     * @return the list of hooked plugins
     */
    public List<String> getPluginNames() {
        return integrationManager.getEnabledIntegrations().stream()
                .map(IntegrationData::getDisplayInfo)
                .flatMap(Collection::stream)
                .map(Triple::getLeft)
                .filter(name -> !"Minecraft".equals(name))
                .sorted()
                .toList();
    }

    /**
     * Gets the BetonQuest integration.
     *
     * @return BetonQuest provided integrations
     */
    public List<IntegrationData> getBetonQuest() {
        return getIntegrationsByPluginName().getOrDefault(BETONQUEST, List.of());
    }

    /**
     * Gets the External integration by their plugin.
     *
     * @return external provided integrations
     */
    public Map<String, List<IntegrationData>> getExternal() {
        final Map<String, List<IntegrationData>> map = getIntegrationsByPluginName();
        map.remove(BETONQUEST);
        return map;
    }

    private Map<String, List<IntegrationData>> getIntegrationsByPluginName() {
        return integrationManager.getEnabledIntegrations().stream()
                .collect(Collectors.groupingBy(data -> data.integrationProvider().getName(),
                        LinkedTreeMap::new, Collectors.toList()));
    }

    private void logSourceAndCollectHologramIntegrators(final List<IntegrationData> dataList, final String name,
                                                        final List<HologramIntegrator> hologramIntegrators) {
        final String hooks = dataList.stream()
                .map(IntegrationData::getDisplayInfo)
                .map(list -> {
                    final int singleElement = 1;
                    if (list.size() == singleElement) {
                        final Triple<String, String, String> triple = list.get(0);
                        return "%s (%s)".formatted(triple.getLeft(), triple.getMiddle());
                    }
                    return list.stream()
                            .map(triple -> "%s (%s)".formatted(triple.getLeft(), triple.getMiddle()))
                            .collect(Collectors.joining(", ", "[", "]"));
                })
                .collect(Collectors.joining(", "));
        if (!hooks.isEmpty()) {
            log.info("Enabled compatibility%sfor %s!".formatted(name, hooks));
        }
        dataList.stream()
                .map(IntegrationData::getIntegration)
                .filter(HologramIntegrator.class::isInstance)
                .map(HologramIntegrator.class::cast)
                .forEach(hologramIntegrators::add);
    }

    /**
     * After all integrations are successfully hooked,
     * this method can be called log loaded compatibility and to activate the hologram provider.
     */
    public void logAndInitHologramProvider() {
        final Map<String, List<IntegrationData>> map = getIntegrationsByPluginName();
        final List<IntegrationData> betonQuest = map.remove(BETONQUEST);
        final List<HologramIntegrator> hologramIntegrators = new ArrayList<>();
        if (betonQuest != null) {
            logSourceAndCollectHologramIntegrators(betonQuest, " ", hologramIntegrators);
        }
        map.forEach((name, data) -> logSourceAndCollectHologramIntegrators(data,
                " from plugin '%s' ".formatted(name), hologramIntegrators));
        hologramProvider = new HologramProvider(hologramIntegrators);
        hologramProvider.postEnable(betonQuestApi);
    }

    /**
     * Disables all loaded integrators.
     */
    public void disable() {
        if (hologramProvider != null) {
            hologramProvider.disable();
        }
    }
}
