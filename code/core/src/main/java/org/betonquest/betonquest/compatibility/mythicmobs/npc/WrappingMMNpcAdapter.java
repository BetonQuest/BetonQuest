package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A Mythic Mob which has not been spawned yet.
 */
public class WrappingMMNpcAdapter implements Npc<ActiveMob> {

    /**
     * Mob type to spawn.
     */
    private final MythicMob type;

    /**
     * Hider for Mobs.
     */
    private final MythicHider mythicHider;

    /**
     * Spawned Mob instance.
     */
    @Nullable
    private Npc<ActiveMob> spawned;

    /**
     * Creates a new empty Adapter with a Mob Type.
     *
     * @param type        mob type to spawn
     * @param mythicHider the hider for mobs
     */
    public WrappingMMNpcAdapter(final MythicMob type, final MythicHider mythicHider) {
        this.type = type;
        this.mythicHider = mythicHider;
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
    public Optional<Location> getLocation() {
        return spawned == null ? Optional.empty() : spawned.getLocation();
    }

    @Override
    public Optional<Location> getEyeLocation() {
        return spawned == null ? Optional.empty() : spawned.getEyeLocation();
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
            spawned = new MythicMobsNpcAdapter(targetMob, mythicHider);
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
