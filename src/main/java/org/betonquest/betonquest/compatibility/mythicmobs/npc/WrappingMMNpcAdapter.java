package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * A Mythic Mob which has not been spawned yet.
 */
public class WrappingMMNpcAdapter implements Npc<ActiveMob> {
    /**
     * An 'empty' location.
     */
    private static final Location EMPTY = new Location(null, 0, 0, 0);

    /**
     * Mob type to spawn.
     */
    private final MythicMob type;

    /**
     * Spawned Mob instance.
     */
    @Nullable
    private Npc<ActiveMob> spawned;

    /**
     * Creates a new empty Adapter with a Mob Type.
     *
     * @param type mob type to spawn
     */
    public WrappingMMNpcAdapter(final MythicMob type) {
        this.type = type;
    }

    @Override
    public ActiveMob getOriginal() {
        if (spawned == null) {
            throw new IllegalStateException("Can't get the original of an not spawned mob!");
        }
        return spawned.getOriginal();
    }

    @Override
    public String getName() {
        return spawned == null ? type.getInternalName() : spawned.getName();
    }

    @Override
    public String getFormattedName() {
        return spawned == null ? type.getDisplayName().get() : spawned.getFormattedName();
    }

    @Override
    public Location getLocation() {
        return spawned == null ? EMPTY : spawned.getLocation();
    }

    @Override
    public Location getEyeLocation() {
        return spawned == null ? EMPTY : spawned.getEyeLocation();
    }

    @Override
    public void teleport(final Location location) {
        if (spawned == null) {
            spawn(location);
        } else {
            spawned.teleport(location);
        }
    }

    @Override
    public boolean isSpawned() {
        return spawned != null && spawned.isSpawned();
    }

    @Override
    public void spawn(final Location location) {
        if (spawned == null || !spawned.isSpawned()) {
            final BukkitAPIHelper apiHelper = new BukkitAPIHelper();
            final Entity entity;
            try {
                entity = apiHelper.spawnMythicMob(type, location, 0);
            } catch (final InvalidMobTypeException ignored) {
                return;
            }
            final ActiveMob targetMob = apiHelper.getMythicMobInstance(entity);
            spawned = new MythicMobsNpcAdapter(targetMob);
        }
    }

    @Override
    public void despawn() {
        if (spawned != null) {
            spawned.despawn();
        }
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        if (spawned != null) {
            spawned.show(onlineProfile);
        }
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        if (spawned != null) {
            spawned.hide(onlineProfile);
        }
    }
}
