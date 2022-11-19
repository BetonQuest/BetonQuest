package org.betonquest.betonquest.compatibility.protocollib.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.CustomLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Glow API class
 */
@CustomLog
public class GlowAPI {

    /**
     * Constructor of Glow API instance
     */
    public GlowAPI() {
    }

    /**
     * Creating and Sending Glow Packet for NPC in async (background).
     *
     * @param npcId   the id of NPC
     * @param color   Set color for glowing entity
     * @param glowing true if entity need to be glowing
     * @param players List of players that can see the glowing NPC
     * @return void
     */
    public CompletableFuture<Void> glowPacketAsync(final int npcId, final ChatColor color, final boolean glowing, final Collection<? extends Player> players) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            LOG.warn("NPC Glow could not update Glowing for npc " + npcId + ": No npc with this id found!");
            return null;
        }
        final Entity entity = npc.getEntity();
        return glowPacketAsync(entity, color, glowing, players);
    }

    /**
     * Creating and Sending Glow Packet for NPC in async (background).
     *
     * @param npcId   the id of NPC
     * @param color   Set color for glowing entity
     * @param glowing true if entity need to be glowing
     * @param player  Target Player that can see the glow
     * @return void
     */
    public CompletableFuture<Void> glowPacketAsync(final int npcId, final ChatColor color, final boolean glowing, final Player player) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            LOG.warn("NPC Glow could not update Glowing for npc " + npcId + ": No npc with this id found!");
            return null;
        }
        final Entity entity = npc.getEntity();
        return glowPacketAsync(entity, color, glowing, player);
    }

    /**
     * Creating and Sending Glow Packet in async (background).
     *
     * @param entity  entity that will get glow
     * @param color   Set color for glowing entity
     * @param glowing true if entity need to be glowing
     * @param player  Target Player that can see the glow
     * @return void
     */
    public CompletableFuture<Void> glowPacketAsync(final Entity entity, final ChatColor color, final boolean glowing, final Player player) {
        final Collection<Entity> entities = Collections.singletonList(entity);
        return glowPacketAsync(entities, color, glowing, player);
    }

    /**
     * Creating and Sending Glow Packet in async (background).
     *
     * @param entity  entity that will get glow
     * @param color   Set color for glowing entity
     * @param glowing true if entity need to be glowing
     * @param players List of players that can see the glowing NPC
     * @return void
     */
    public CompletableFuture<Void> glowPacketAsync(final Entity entity, final ChatColor color, final boolean glowing, final Collection<? extends Player> players) {
        return CompletableFuture.allOf(players.parallelStream().map(player -> glowPacketAsync(entity, color, glowing, player)).toArray(CompletableFuture[]::new));
    }

    /**
     * Creating and Sending Glow Packet for a list of entities in async (background).
     *
     * @param entities List of entities that will get glow
     * @param color    Set color for glowing entity
     * @param glowing  true if entity need to be glowing
     * @param player   Target Player that can see the glow
     * @return void
     */
    public CompletableFuture<Void> glowPacketAsync(final Collection<Entity> entities, final ChatColor color, final boolean glowing, final Player player) {
        return CompletableFuture.allOf(entities.parallelStream().map(entity -> sendGlowPacketAsync(entity, glowing, player)).toArray(CompletableFuture[]::new))
                .thenRun(() -> {
                    if (glowing) {
                        sendTeamPacketAsync(entities, color, Mode.TEAM_CREATED, player);
                    } else {
                        sendTeamPacketAsync(entities, color, Mode.TEAM_REMOVED, player);
                    }
                });
    }

    /**
     * Creating and Sending Glow Packet in async (background)
     *
     * @param entity  Target entity that will get glow
     * @param glowing true if entity need to be glowing
     * @param player  Target Player that can see the glow
     * @return void
     */
    private CompletableFuture<Void> sendGlowPacketAsync(final Entity entity, final boolean glowing, final Player player) {
        return CompletableFuture.runAsync(() -> {
            final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
            final PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

            final int entityId = entity.getEntityId();

            final WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity);
            final List<WrappedWatchableObject> dataWatcherObjects = dataWatcher.getWatchableObjects();

            byte entityByte = 0x00;
            if (!dataWatcherObjects.isEmpty()) {
                entityByte = (byte) dataWatcherObjects.get(0).getValue();
            }
            if (glowing) {
                entityByte = (byte) (entityByte | 0x40);
            } else {
                entityByte = (byte) (entityByte & ~0x40);
                final Collection<Entity> entities = Collections.singletonList(entity);
                sendTeamPacketAsync(entities, null, Mode.TEAM_REMOVED, player);
            }

            final WrappedWatchableObject wrappedMetadata = new WrappedWatchableObject(dataWatcherObject, entityByte);
            final List<WrappedWatchableObject> metadata = Collections.singletonList(wrappedMetadata);

            packetContainer.getIntegers().write(0, entityId);
            packetContainer.getWatchableCollectionModifier().write(0, metadata);

            sendPacket(player, packetContainer);
        });
    }

    /**
     * Sending a PacketContainer.
     *
     * @param player          Send PacketContainer to Player
     * @param packetContainer PacketContainer that will get send
     */
    private static void sendPacket(final Player player, final PacketContainer packetContainer) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (final InvocationTargetException e) {
            LOG.warn("Error while sending the Packet", e);
        }
    }

    /**
     * Creating and Sending Team Packet in async (background)
     *
     * @param entities   List of entities that will be on team
     * @param color      Color of the team
     * @param packetMode Mode of the team
     * @param player     Player that have the team
     * @return void
     */
    public static CompletableFuture<Void> sendTeamPacketAsync(final Collection<? extends Entity> entities, final ChatColor color, final Mode packetMode, final Player player) {
        return CompletableFuture.runAsync(() -> {
            final PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getIntegers().write(0, packetMode.ordinal());

            packet.getStrings().write(0, "Color#" + color.name());
            if (packetMode == Mode.TEAM_REMOVED) {
                sendPacket(player, packet);
            }
            packet.getOptionalStructures().read(0).map((structure) ->
                    structure.getEnumModifier(ChatColor.class,
                                    MinecraftReflection.getMinecraftClass("EnumChatFormat"))
                            .write(0, color));

            @SuppressWarnings("unchecked") final Collection<String> entries = packet.getSpecificModifier(Collection.class)
                    .read(0);
            entities
                    .parallelStream()
                    .map(entity -> {
                        if (entity instanceof OfflinePlayer) {
                            return entity.getName();
                        } else {
                            return entity.getUniqueId().toString();
                        }
                    })
                    .forEach(entries::add);
            sendPacket(player, packet);
        });
    }

    /**
     * List mode for Team Packet
     */
    protected enum Mode {
        /**
         * Create a Team
         */
        TEAM_CREATED,
        /**
         * Remove Existed Team
         */
        TEAM_REMOVED,
        /**
         * Update Team
         */
        TEAM_UPDATED,
        /**
         * Added players to the Team
         */
        PLAYERS_ADDED,
        /**
         * Remove players from the team
         */
        PLAYERS_REMOVED
    }
}
