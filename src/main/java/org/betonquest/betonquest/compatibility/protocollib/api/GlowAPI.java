package org.betonquest.betonquest.compatibility.protocollib.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.CustomLog;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Creates and sends glow packets using ProtocolLib.
 * May be called asynchronously.
 */
@CustomLog
public class GlowAPI {

    /**
     * Bukkit Scoreboard to handle the team of the glowing {@link Entity}
     */
    private final Scoreboard board;

    /**
     * Constructor of Glow API instance
     */
    public GlowAPI() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    /**
     * Creates and sends a team packet.
     *
     * @param entities   List of {@link Entity} that will be on team
     * @param color      Color of the team from {@link ChatColor}
     * @param packetMode {@link Mode} of the team
     * @param profile    {@link OnlineProfile} that have the team
     */
    public void sendTeamGlowPacket(final Collection<Entity> entities, final ChatColor color, final Mode packetMode, final OnlineProfile profile) {
        final String teamName = "Color#" + color.name();
        final Team team = Optional
                .ofNullable(board.getTeam(teamName))
                .orElseGet(() -> board.registerNewTeam(teamName));
        team.setColor(color);

        if (packetMode == Mode.PLAYERS_ADDED) {
            team.addEntities(entities);
        }

        if (packetMode == Mode.PLAYERS_REMOVED && team.removeEntities(entities)) {
            profile.getPlayer().setScoreboard(board);
            return;
        }

        if (packetMode == Mode.TEAM_REMOVED) {
            team.unregister();
        }
        profile.getPlayer().setScoreboard(board);
    }

    /**
     * Sending a PacketContainer.
     *
     * @param profile         Send PacketContainer to {@link OnlineProfile}
     * @param packetContainer {@link PacketContainer} that will get send
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
     * @param entity   {@link Entity} that will get glow
     * @param color    the glow color from {@link ChatColor}
     * @param willGlow the new glow state of the {@link Entity}
     * @param profiles List of {@link OnlineProfile} that can see the glowing {@link Entity}
     * @see #sendGlowPacket(Entity, ChatColor, boolean, OnlineProfile)
     */
    public void sendGlowPacket(final Entity entity, final ChatColor color, final boolean willGlow, final Collection<? extends OnlineProfile> profiles) {
        profiles.parallelStream().forEach(profile -> sendGlowPacket(entity, color, willGlow, profile));
    }

    /**
     * Creating and Sending Glow Packet in async (background).
     *
     * @param entity   {@link Entity} that will get glow
     * @param color    the glow color from {@link ChatColor}
     * @param willGlow the new glow state of the {@link Entity}
     * @param profile  {@link OnlineProfile} that can see the glowing {@link Entity}
     * @see #sendGlowPacket(Collection, ChatColor, boolean, OnlineProfile)
     */
    public void sendGlowPacket(final Entity entity, final ChatColor color, final boolean willGlow, final OnlineProfile profile) {
        if (entity != null) {
            final Collection<Entity> entities = Collections.singletonList(entity);
            sendGlowPacket(entities, color, willGlow, profile);
        }
    }

    /**
     * Creates and sends a glow packet for a list of entities.
     *
     * @param entities a list of {@link Entity} that will glow
     * @param color    the glow color from {@link ChatColor}
     * @param willGlow the new glow state of the {@link Entity}
     * @param profile  {@link OnlineProfile} that can see the glowing {@link Entity}
     */
    public void sendGlowPacket(final Collection<Entity> entities, final ChatColor color, final boolean willGlow, final OnlineProfile profile) {
        entities
                .parallelStream()
                .forEach(entity -> createAndSendGlowPacket(entity, willGlow, profile));

        if (willGlow) {
            sendTeamGlowPacket(entities, color, Mode.PLAYERS_ADDED, profile);
        } else {
            sendTeamGlowPacket(entities, color, Mode.PLAYERS_REMOVED, profile);
        }
    }

    /**
     * Creates and sends the glow packet.
     *
     * @param entity   target {@link Entity} that will get glow
     * @param willGlow the new glow state of the {@link Entity}
     * @param profile  target {@link OnlineProfile} that can see the glow
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
    public enum Mode {
        /**
         * Create a {@link Team}
         */
        TEAM_CREATED,
        /**
         * Remove Existed {@link Team}
         */
        TEAM_REMOVED,
        /**
         * Update {@link Team}
         */
        TEAM_UPDATED,
        /**
         * Added players to the {@link Team}
         */
        PLAYERS_ADDED,
        /**
         * Remove players from the {@link Team}
         */
        PLAYERS_REMOVED
    }
}
