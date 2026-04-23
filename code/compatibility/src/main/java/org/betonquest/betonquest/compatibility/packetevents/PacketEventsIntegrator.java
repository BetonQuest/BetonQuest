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
import org.betonquest.betonquest.api.bukkit.BukkitManager;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.compatibility.packetevents.action.FreezeActionFactory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.PacketEventsInterceptorFactory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.ChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.NoneChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.interceptor.history.PacketChatHistory;
import org.betonquest.betonquest.compatibility.packetevents.passenger.FakeArmorStandPassengerController;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.menu.MenuConvIOFactory;
import org.betonquest.betonquest.conversation.menu.input.ConversationAction;
import org.betonquest.betonquest.conversation.menu.input.ConversationSession;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.lib.version.MinecraftVersion;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Function;

/**
 * Integrator for PacketEvents.
 */
public class PacketEventsIntegrator implements Integration {

    /**
     * The minimum required version of packetevents.
     */
    public static final String REQUIRED_VERSION = "2.9.5";

    /**
     * Function to create chat message packets based on server version.
     */
    // TODO version switch:
    //  Remove this code when only 1.19.0+ is supported
    public static final Function<Component, PacketWrapper<?>> MESSAGE_FUNCTION = MinecraftVersion.isCompatibleWith("1.19.0")
            ? message -> new WrapperPlayServerSystemChatMessage(false, message)
            : message -> new WrapperPlayServerChatMessage(new ChatMessage_v1_16(message,
            ChatTypes.CHAT, new UUID(0L, 0L)));

    /**
     * The default constructor.
     */
    public PacketEventsIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final PacketEventsAPI<?> packetEventsAPI = PacketEvents.getAPI();

        final BetonQuest plugin = BetonQuest.getInstance();
        final CoreComponentLoader componentLoader = plugin.getComponentLoader();
        final ConfigAccessor pluginConfig = plugin.getPluginConfig();

        final TriFunction<Player, ConversationAction, Boolean, ConversationSession> inputFunction = (player, control, setSpeed) ->
                new FakeArmorStandPassengerController(plugin, packetEventsAPI, player, control, setSpeed);
        componentLoader.get(ConversationIORegistry.class).register("packetevents",
                new MenuConvIOFactory(api.loggerFactory(), pluginConfig, plugin, api.localizations(), inputFunction, componentLoader.get(TextParser.class),
                        api.fonts(), componentLoader.get(ConversationColors.class)));

        final boolean displayHistory = pluginConfig.getBoolean("conversation.interceptor.display_history");
        final ChatHistory chatHistory = displayHistory ? getPacketChatHistory(packetEventsAPI, api.bukkit()) : new NoneChatHistory();
        componentLoader.get(InterceptorRegistry.class).register("packetevents", new PacketEventsInterceptorFactory(packetEventsAPI, chatHistory));

        api.actions().registry().register("freeze", new FreezeActionFactory(plugin, packetEventsAPI));
    }

    private PacketChatHistory getPacketChatHistory(final PacketEventsAPI<?> packetEventsAPI, final BukkitManager bukkitManager) {
        final PacketChatHistory chatHistory = new PacketChatHistory(packetEventsAPI, 100);
        bukkitManager.registerEvents(chatHistory);
        packetEventsAPI.getEventManager().registerListener(chatHistory, PacketListenerPriority.MONITOR);
        return chatHistory;
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
