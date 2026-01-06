package org.betonquest.betonquest.compatibility.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.function.TriFunction;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.packetevents.action.FreezeActionFactory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.PacketEventsInterceptorFactory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.ChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.NoneChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.PacketChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.passenger.FakeArmorStandPassengerController;
import org.betonquest.betonquest.conversation.menu.MenuConvIOFactory;
import org.betonquest.betonquest.conversation.menu.input.ConversationAction;
import org.betonquest.betonquest.conversation.menu.input.ConversationSession;
import org.betonquest.betonquest.versioning.MinecraftVersion;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;
import java.util.function.Function;

/**
 * Integrator for PacketEvents.
 */
public class PacketEventsIntegrator implements Integrator {

    /**
     * Function to create chat message packets based on server version.
     */
    // TODO version switch:
    //  Remove this code when only 1.19.0+ is supported
    public static final Function<Component, PacketWrapper<?>> MESSAGE_FUNCTION = new MinecraftVersion().isCompatibleWith("1.19.0")
            ? message -> new WrapperPlayServerSystemChatMessage(false, message)
            : message -> new WrapperPlayServerChatMessage(new ChatMessage_v1_16(message,
            ChatTypes.CHAT, new UUID(0L, 0L)));

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
        if (comparator.isOlderThan(packetEventsVersion, new Version("2.9.5"))) {
            throw new UnsupportedVersionException(packetEvents, "2.9.5");
        }

        final PacketEventsAPI<?> packetEventsAPI = PacketEvents.getAPI();

        final BetonQuest plugin = BetonQuest.getInstance();
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();

        final TriFunction<Player, ConversationAction, Boolean, ConversationSession> inputFunction = (player, control, setSpeed) ->
                new FakeArmorStandPassengerController(plugin, packetEventsAPI, player, control, setSpeed);
        api.getFeatureRegistries().conversationIO().register("packetevents", new MenuConvIOFactory(inputFunction, plugin, plugin.getTextParser(),
                plugin.getFontRegistry(), pluginConfig, plugin.getConversationColors()));

        final boolean displayHistory = pluginConfig.getBoolean("conversation.interceptor.display_history");
        final ChatHistory chatHistory = displayHistory ? getPacketChatHistory(packetEventsAPI, pluginManager, plugin) : new NoneChatHistory();
        api.getFeatureRegistries().interceptor().register("packetevents", new PacketEventsInterceptorFactory(packetEventsAPI, chatHistory));

        api.getQuestRegistries().event().register("freeze", new FreezeActionFactory(plugin, packetEventsAPI, api.getLoggerFactory()));
    }

    private PacketChatHistory getPacketChatHistory(final PacketEventsAPI<?> packetEventsAPI, final PluginManager pluginManager, final BetonQuest plugin) {
        final PacketChatHistory chatHistory = new PacketChatHistory(packetEventsAPI, 100);
        pluginManager.registerEvents(chatHistory, plugin);
        packetEventsAPI.getEventManager().registerListener(chatHistory, PacketListenerPriority.MONITOR);
        return chatHistory;
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
