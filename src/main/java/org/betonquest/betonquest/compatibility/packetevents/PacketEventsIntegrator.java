package org.betonquest.betonquest.compatibility.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.packetevents.conversation.MenuConvIOFactory;
import org.betonquest.betonquest.compatibility.packetevents.event.FreezeEventFactory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.ChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.PacketEventsInterceptorFactory;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Integrator for PacketEvents.
 */
public class PacketEventsIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public PacketEventsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final Plugin packetEvents = pluginManager.getPlugin("packetevents");
        final Version packetEventsVersion = new Version(packetEvents.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.isOtherNewerThanCurrent(packetEventsVersion, new Version("2.9.5"))) {
            throw new UnsupportedVersionException(packetEvents, "2.9.5");
        }

        final PacketEventsAPI<?> packetEventsAPI = PacketEvents.getAPI();

        final BetonQuest plugin = BetonQuest.getInstance();
        api.getFeatureRegistries().conversationIO().register("menu", new MenuConvIOFactory(packetEventsAPI, plugin, plugin.getTextParser(),
                plugin.getFontRegistry(), plugin.getPluginConfig(), plugin.getConversationColors()));

        final ChatHistory chatHistory = new ChatHistory(packetEventsAPI, 100);
        pluginManager.registerEvents(chatHistory, plugin);
        packetEventsAPI.getEventManager().registerListener(chatHistory, PacketListenerPriority.MONITOR);
        api.getFeatureRegistries().interceptor().register("packetevents", new PacketEventsInterceptorFactory(packetEventsAPI, chatHistory));

        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        api.getQuestRegistries().event().register("freeze", new FreezeEventFactory(packetEventsAPI, api.getLoggerFactory(), data));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
