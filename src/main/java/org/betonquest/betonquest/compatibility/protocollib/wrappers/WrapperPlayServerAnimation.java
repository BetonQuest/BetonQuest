package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@SuppressWarnings("PMD.CommentRequired")
public class WrapperPlayServerAnimation extends PacketHandlerDecorator {

    public static final PacketType TYPE = PacketType.Play.Server.ANIMATION;

    public WrapperPlayServerAnimation() {
        this(new DefaultPacketHandler(TYPE));
    }

    public WrapperPlayServerAnimation(final PacketContainer packet) {
        this(new DefaultPacketHandler(packet, TYPE));
    }

    public WrapperPlayServerAnimation(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return getHandle().getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(final int value) {
        getHandle().getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(final World world) {
        return getHandle().getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(final PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve Animation.
     * <p>
     * Notes: animation ID
     *
     * @return The current Animation
     */
    public int getAnimation() {
        return getHandle().getIntegers().read(1);
    }

    /**
     * Set Animation.
     *
     * @param value - new value.
     */
    public void setAnimation(final int value) {
        getHandle().getIntegers().write(1, value);
    }

}
