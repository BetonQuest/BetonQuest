package org.betonquest.betonquest.compatibility.protocollib.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.CustomLog;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Creates and sends glow packets using ProtocolLib.
 * May be called asynchronously.
 */
@CustomLog
public class GlowAPI {

    /**
     * Constructor of Glow API instance
     */
    public GlowAPI() {
    }

    /**
     * Creates and sends a team packet.
     *
     * @param entities   List of entities that will be on team
     * @param color      Color of the team
     * @param packetMode Mode of the team
     * @param profile    Player that have the team
     */
    public void sendTeamGlowPacket(final Collection<? extends Entity> entities, final ChatColor color, final Mode packetMode, final OnlineProfile profile) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, packetMode.ordinal());

        if (packetMode == Mode.TEAM_REMOVED) {
            sendPacket(profile, packet);
            return;
        }
        packet.getStrings().write(0, "Color#" + color.name());
        packet.getOptionalStructures().read(0).map((structure) ->
                structure.getEnumModifier(ChatColor.class,
                                MinecraftReflection.getMinecraftClass("EnumChatFormat"))
                        .write(0, color));

        @SuppressWarnings("unchecked") final Collection<String> entries = packet.getSpecificModifier(Collection.class).read(0);
        entities.stream()
                .map(entity -> {
                    if (entity instanceof OfflinePlayer) {
                        return entity.getName();
                    } else {
                        return entity.getUniqueId().toString();
                    }
                })
                .forEach(entries::add);
        sendPacket(profile, packet);
    }

    /**
     * Sending a PacketContainer.
     *
     * @param profile         Send PacketContainer to Player
     * @param packetContainer PacketContainer that will get send
     */
    private void sendPacket(final OnlineProfile profile, final PacketContainer packetContainer) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(profile.getPlayer(), packetContainer);
        } catch (final InvocationTargetException e) {
            LOG.warn("Error while sending the Packet", e);
        }
    }

    /**
     * Creates and send a glow packet.
     *
     * @param entity   entity that will get glow
     * @param color    the glow color
     * @param willGlow the new glow state of the entity
     * @param profiles List of profiles that can see the glowing entity
     */
    public void sendGlowPacket(final Entity entity, final ChatColor color, final boolean willGlow, final Collection<? extends OnlineProfile> profiles) {
        profiles.forEach(profile -> sendGlowPacket(entity, color, willGlow, profile));
    }

    /**
     * Creating and Sending Glow Packet in async (background).
     *
     * @param entity   entity that will glow
     * @param color    the glow color
     * @param willGlow the new glow state of the entity
     * @param profile  profile that can see the glow
     */
    public void sendGlowPacket(final Entity entity, final ChatColor color, final boolean willGlow, final OnlineProfile profile) {
        final Collection<Entity> entities = Collections.singletonList(entity);
        sendGlowPacket(entities, color, willGlow, profile);
    }

    /**
     * Creates and sends a glow packet for a list of entities.
     *
     * @param entities a list of entities that will glow
     * @param color    the glow color
     * @param willGlow the new glow state of the entity
     * @param profile  profile that can see the glow
     */
    public void sendGlowPacket(final Collection<Entity> entities, final ChatColor color, final boolean willGlow, final OnlineProfile profile) {
        entities.forEach(entity -> createAndSendGlowPacket(entity, willGlow, profile));

        if (willGlow) {
            sendTeamGlowPacket(entities, color, Mode.TEAM_CREATED, profile);
        } else {
            sendTeamGlowPacket(entities, color, Mode.TEAM_REMOVED, profile);
        }
    }

    /**
     * Creates and sends the glow packet.
     *
     * @param entity   target entity that will get glow
     * @param willGlow the new glow state of the entity
     * @param profile  target player that can see the glow
     */
    private void createAndSendGlowPacket(final Entity entity, final boolean willGlow, final OnlineProfile profile) {
        final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
        final PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        final int entityId = entity.getEntityId();

        final WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity);
        final List<WrappedWatchableObject> dataWatcherObjects = dataWatcher.getWatchableObjects();

        byte entityByte = 0x00;
        if (!dataWatcherObjects.isEmpty()) {
            entityByte = (byte) dataWatcherObjects.get(0).getValue();
        }
        if (willGlow) {
            entityByte = (byte) (entityByte | 0x40);
        } else {
            entityByte = (byte) (entityByte & ~0x40);
        }

        final WrappedWatchableObject wrappedMetadata = new WrappedWatchableObject(dataWatcherObject, entityByte);
        final List<WrappedWatchableObject> metadata = Collections.singletonList(wrappedMetadata);

        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getWatchableCollectionModifier().write(0, metadata);

        sendPacket(profile, packetContainer);
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
