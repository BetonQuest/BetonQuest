package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import org.betonquest.betonquest.lib.version.MinecraftVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PacketChatHistoryTest {

    @BeforeAll
    static void initializeMessageFunction() throws ClassNotFoundException {
        try (MockedStatic<MinecraftVersion> version = mockStatic(MinecraftVersion.class)) {
            version.when(() -> MinecraftVersion.isCompatibleWith("1.19.0")).thenReturn(true);
            Class.forName("org.betonquest.betonquest.compatibility.packetevents.PacketEventsIntegrator");
        }
    }

    private Player player() {
        final Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        return player;
    }

    private PacketSendEvent event(final Player player) {
        final PacketSendEvent event = mock(PacketSendEvent.class);
        when(event.getPacketType()).thenReturn(PacketType.Play.Server.SYSTEM_CHAT_MESSAGE);
        when(event.getPlayer()).thenReturn(player);
        return event;
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void evictsOldestMessagesAndClearsHistoryOnQuit() {
        final PacketEventsAPI<?> api = mock(PacketEventsAPI.class, RETURNS_DEEP_STUBS);
        final Player player = player();
        final User user = mock(User.class);
        when(api.getPlayerManager().getUser(player)).thenReturn(user);
        final PacketChatHistory history = new PacketChatHistory(api, 2);
        final PacketSendEvent event = event(player);
        try (MockedConstruction<WrapperPlayServerSystemChatMessage> packets =
                     mockConstruction(WrapperPlayServerSystemChatMessage.class)) {
            history.onPacketSend(event);
            history.onPacketSend(event);
            history.onPacketSend(event);
            final PacketWrapper<?> second = packets.constructed().get(3);
            final PacketWrapper<?> third = packets.constructed().get(5);
            history.sendHistory(player);
            final InOrder order = inOrder(user);
            order.verify(user).sendPacketSilently(second);
            order.verify(user).sendPacketSilently(third);
            order.verifyNoMoreInteractions();

            final PlayerQuitEvent quitEvent = mock(PlayerQuitEvent.class);
            when(quitEvent.getPlayer()).thenReturn(player);
            history.onPlayerQuit(quitEvent);
            clearInvocations(user);
            history.sendHistory(player);
            verify(user).sendPacketSilently(packets.constructed().get(6));
            verifyNoMoreInteractions(user);
        }
    }

    @Test
    void includesPacketsArrivingDuringReplay() {
        final PacketEventsAPI<?> api = mock(PacketEventsAPI.class, RETURNS_DEEP_STUBS);
        final Player player = player();
        final User user = mock(User.class);
        when(api.getPlayerManager().getUser(player)).thenReturn(user);
        final PacketChatHistory history = new PacketChatHistory(api, 3);
        final PacketSendEvent event = event(player);
        try (MockedConstruction<WrapperPlayServerSystemChatMessage> packets =
                     mockConstruction(WrapperPlayServerSystemChatMessage.class)) {
            history.onPacketSend(event);
            history.onPacketSend(event);
            final List<PacketWrapper<?>> sent = new ArrayList<>();
            doAnswer(invocation -> {
                final boolean firstHistoryPacket = sent.size() == 1;
                sent.add(invocation.getArgument(0));
                if (firstHistoryPacket) {
                    history.onPacketSend(event);
                }
                return null;
            }).when(user).sendPacketSilently(any(PacketWrapper.class));
            history.sendHistory(player);
            assertEquals(List.of(packets.constructed().get(4), packets.constructed().get(1),
                            packets.constructed().get(3), packets.constructed().get(6)), sent,
                    "Replay should include packets that arrive while it is running");
        }
    }

    @Test
    @SuppressWarnings({"PMD.DoNotUseThreads", "PMD.CloseResource"})
    void capturesReplaysAndRemovesHistoriesFromConcurrentThreads() throws Exception {
        final PacketEventsAPI<?> api = mock(PacketEventsAPI.class, RETURNS_DEEP_STUBS);
        when(api.getPlayerManager().getUser(any(Player.class))).thenReturn(mock(User.class));
        final PacketChatHistory history = new PacketChatHistory(api, 2);
        final Player sharedPlayer = player();
        final CountDownLatch start = new CountDownLatch(1);
        final List<Future<?>> tasks = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            for (int thread = 0; thread < 8; thread++) {
                final Player player = thread % 2 == 0 ? sharedPlayer : player();
                tasks.add(executor.submit(() -> {
                    final PacketSendEvent event = event(player);
                    try (MockedConstruction<WrapperPlayServerSystemChatMessage> ignored =
                                 mockConstruction(WrapperPlayServerSystemChatMessage.class)) {
                        assertTrue(start.await(10, TimeUnit.SECONDS), "Workers should start together");
                        for (int iteration = 0; iteration < 500; iteration++) {
                            history.onPacketSend(event);
                            history.sendHistory(player);
                            final PlayerQuitEvent quitEvent = mock(PlayerQuitEvent.class);
                            when(quitEvent.getPlayer()).thenReturn(player);
                            history.onPlayerQuit(quitEvent);
                        }
                    }
                    return null;
                }));
            }
            start.countDown();
            for (final Future<?> task : tasks) {
                task.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
