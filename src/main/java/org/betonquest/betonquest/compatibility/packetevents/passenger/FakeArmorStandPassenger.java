package org.betonquest.betonquest.compatibility.packetevents.passenger;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.compatibility.packetevents.conversation.input.ConversationSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created a fake armor stand amd mounts the player on it.
 */
public class FakeArmorStandPassenger implements ConversationSession {
    /**
     * The PacketEvents API instance.
     */
    protected final PacketEventsAPI<?> packetEventsAPI;

    /**
     * The player to mount.
     */
    protected final Player player;

    /**
     * The armor stand entity ID.
     */
    private final int armorStandId;

    /**
     * Constructs a new FakeArmorStandPassenger that also created a new entity ID for the armor stand.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param player          the player to mount
     */
    public FakeArmorStandPassenger(final PacketEventsAPI<?> packetEventsAPI, final Player player) {
        this.packetEventsAPI = packetEventsAPI;
        this.player = player;
        this.armorStandId = Bukkit.getUnsafe().nextEntityId();
    }

    /**
     * Gets the location on the top of the block below the player.
     * This prevents the armor stand from spawning in the air.
     * <p>
     * This is done by getting the bounding box of the player.
     * Then all bounding boxes of the blocks in the bounding box of the player are checked for collision.
     * The highest collision is then returned.
     * If no collision is found, the process is repeated with the player bounding box shifted down by 1.
     *
     * @param player the player to get the location for
     * @return the location on the top of the block below the player
     */
    public static Location getBlockBelowPlayer(final Player player) {
        if (player.isFlying()) {
            return player.getLocation();
        }

        final BoundingBox playerBoundingBox = player.getBoundingBox();
        playerBoundingBox.shift(0, -(playerBoundingBox.getMinY() % 1), 0);
        while (playerBoundingBox.getMinY() >= player.getWorld().getMinHeight()) {
            final Set<Block> blocks = getBlocksInBoundingBox(player.getWorld(), playerBoundingBox);

            final List<BoundingBox> boundingBoxes = blocks.stream()
                    .map(block -> block.getCollisionShape().getBoundingBoxes().stream()
                            .map(box -> box.shift(block.getLocation())).toList())
                    .flatMap(Collection::stream)
                    .filter(box -> box.overlaps(playerBoundingBox))
                    .toList();

            if (!boundingBoxes.isEmpty()) {
                final Optional<Double> maxY = boundingBoxes.stream()
                        .map(BoundingBox::getMaxY)
                        .max(Double::compareTo);
                final Location location = player.getLocation();
                location.setY(maxY.get());
                return location;
            }
            playerBoundingBox.shift(0, -1, 0);
        }
        return player.getLocation();
    }

    /**
     * Get the blocks that are at the bottom corners of the player's bounding box.
     * This could be 1, 2 or 4 blocks depending on the player's position.
     *
     * @param world             the world the player is in
     * @param playerBoundingBox the bounding box of the player
     * @return the blocks in the bounding box
     */
    private static Set<Block> getBlocksInBoundingBox(final World world, final BoundingBox playerBoundingBox) {
        final Set<Block> blocks = new HashSet<>();
        blocks.add(new Location(world, playerBoundingBox.getMinX(), playerBoundingBox.getMinY(), playerBoundingBox.getMinZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMinX(), playerBoundingBox.getMinY(), playerBoundingBox.getMaxZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMaxX(), playerBoundingBox.getMinY(), playerBoundingBox.getMinZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMaxX(), playerBoundingBox.getMinY(), playerBoundingBox.getMaxZ()).getBlock());
        return blocks;
    }

    @Override
    public final void begin() {
        mount(getBlockBelowPlayer(player));
    }

    /**
     * Spawns a fake armor stand and mounts the player on it.
     *
     * @param location the location to mount at
     */
    public void mount(final Location location) {
        // TODO version switch:
        //  Remove this code when only 1.20.2+ is supported
        final double heightFix = PaperLib.isVersion(20, 2) ? -0.375 : -0.131_25;
        final Vector3d position = new Vector3d(location.getX(), location.getY() - 1 + heightFix, location.getZ());

        final WrapperPlayServerSpawnEntity standSpawnPacket = new WrapperPlayServerSpawnEntity(armorStandId,
                Optional.empty(), EntityTypes.ARMOR_STAND, position, 0, 0, 0, 0,
                Optional.empty());

        final WrapperPlayServerUpdateAttributes standAttributesPacket = new WrapperPlayServerUpdateAttributes(armorStandId,
                List.of(new WrapperPlayServerUpdateAttributes.Property(Attributes.MAX_HEALTH, 0.0, List.of())));

        final WrapperPlayServerEntityMetadata standMetadataPacket = new WrapperPlayServerEntityMetadata(armorStandId,
                List.of(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20)));

        final WrapperPlayServerSetPassengers standPassengersPacket = new WrapperPlayServerSetPassengers(
                armorStandId, new int[]{player.getEntityId()});

        packetEventsAPI.getPlayerManager().sendPacket(player, standSpawnPacket);
        packetEventsAPI.getPlayerManager().sendPacket(player, standAttributesPacket);
        packetEventsAPI.getPlayerManager().sendPacket(player, standMetadataPacket);
        packetEventsAPI.getPlayerManager().sendPacket(player, standPassengersPacket);

        player.sendActionBar(Component.empty());
    }

    @Override
    public final void end() {
        unmount();
    }

    /**
     * Unmounts the player and destroys the fake armor stand.
     */
    public void unmount() {
        final WrapperPlayServerDestroyEntities standDestroyPacket = new WrapperPlayServerDestroyEntities(armorStandId);
        packetEventsAPI.getPlayerManager().sendPacket(player, standDestroyPacket);
    }
}
