/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class WrapperPlayServerSpawnEntityLiving extends AbstractPacket {
    public static final PacketType TYPE =
            PacketType.Play.Server.SPAWN_ENTITY_LIVING;

    private static PacketConstructor entityConstructor;

    public WrapperPlayServerSpawnEntityLiving() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityLiving(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerSpawnEntityLiving(Entity entity) {
        super(fromEntity(entity), TYPE);
    }

    // Useful constructor
    private static PacketContainer fromEntity(Entity entity) {
        if (entityConstructor == null)
            entityConstructor =
                    ProtocolLibrary.getProtocolManager()
                            .createPacketConstructor(TYPE, entity);
        return entityConstructor.createPacket(entity);
    }

    /**
     * Retrieve entity ID.
     *
     * @return The current EID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public UUID getUniqueId() {
        return handle.getUUIDs().read(0);
    }

    public void setUniqueId(UUID value) {
        handle.getUUIDs().write(0, value);
    }

    /**
     * Retrieve the type of mob.
     *
     * @return The current Type
     */
    @SuppressWarnings("deprecation")
    public EntityType getType() {
        return EntityType.fromId(handle.getIntegers().read(1));
    }

    /**
     * Set the type of mob.
     *
     * @param value - new value.
     */
    @SuppressWarnings("deprecation")
    public void setType(EntityType value) {
        handle.getIntegers().write(1, (int) value.getTypeId());
    }

    /**
     * Retrieve the x position of the object.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     *
     * @return The current X
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Set the x position of the object.
     *
     * @param value - new value.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieve the y position of the object.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     *
     * @return The current y
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Set the y position of the object.
     *
     * @param value - new value.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieve the z position of the object.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     *
     * @return The current z
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Set the z position of the object.
     *
     * @param value - new value.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieve the yaw.
     *
     * @return The current Yaw
     */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    /**
     * Set the yaw of the spawned mob.
     *
     * @param value - new yaw.
     */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieve the pitch.
     *
     * @return The current pitch
     */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    /**
     * Set the pitch of the spawned mob.
     *
     * @param value - new pitch.
     */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieve the yaw of the mob's head.
     *
     * @return The current yaw.
     */
    public float getHeadPitch() {
        return (handle.getBytes().read(2) * 360.F) / 256.0F;
    }

    /**
     * Set the yaw of the mob's head.
     *
     * @param value - new yaw.
     */
    public void setHeadPitch(float value) {
        handle.getBytes().write(2, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieve the velocity in the x axis.
     *
     * @return The current velocity X
     */
    public double getVelocityX() {
        return handle.getIntegers().read(2) / 8000.0D;
    }

    /**
     * Set the velocity in the x axis.
     *
     * @param value - new value.
     */
    public void setVelocityX(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }

    /**
     * Retrieve the velocity in the y axis.
     *
     * @return The current velocity y
     */
    public double getVelocityY() {
        return handle.getIntegers().read(3) / 8000.0D;
    }

    /**
     * Set the velocity in the y axis.
     *
     * @param value - new value.
     */
    public void setVelocityY(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }

    /**
     * Retrieve the velocity in the z axis.
     *
     * @return The current velocity z
     */
    public double getVelocityZ() {
        return handle.getIntegers().read(4) / 8000.0D;
    }

    /**
     * Set the velocity in the z axis.
     *
     * @param value - new value.
     */
    public void setVelocityZ(double value) {
        handle.getIntegers().write(4, (int) (value * 8000.0D));
    }

    /**
     * Retrieve the data watcher.
     * <p>
     * Content varies by mob, see Entities.
     *
     * @return The current Metadata
     */
    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }

    /**
     * Set the data watcher.
     *
     * @param value - new value.
     */
    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
}