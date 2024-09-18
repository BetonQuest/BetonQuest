package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;
import java.util.Optional;

/**
 * MythicMobs {@link Npc} Adapter.
 *
 * @param activeMob The ActiveMob instance.
 */
public record MythicMobsNpcAdapter(ActiveMob activeMob) implements Npc<ActiveMob> {
    @Override
    public ActiveMob getOriginal() {
        return activeMob;
    }

    @Override
    public String getName() {
        return activeMob.getName();
    }

    @Override
    public String getFormattedName() {
        return Objects.requireNonNullElse(activeMob.getDisplayName(), getName());
    }

    @Override
    public Location getLocation() {
        return activeMob.getEntity().getBukkitEntity().getLocation();
    }

    @Override
    public Location getEyeLocation() {
        final Entity entity = activeMob.getEntity().getBukkitEntity();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getEyeLocation();
        }
        return entity.getLocation();
    }

    @Override
    public void teleport(final Location location) {
        activeMob.getEntity().teleport(new AbstractLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public boolean isSpawned() {
        return activeMob.getEntity().isValid();
    }

    @Override
    public void spawn(final Location location) {
        // Already existent
    }

    @Override
    public void despawn() {
        activeMob.despawn();
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        Optional.ofNullable(MythicHider.getInstance()).ifPresent(hider ->
                hider.show(onlineProfile, activeMob.getEntity().getBukkitEntity()));
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        Optional.ofNullable(MythicHider.getInstance()).ifPresent(hider ->
                hider.hide(onlineProfile, activeMob.getEntity().getBukkitEntity()));
    }
}
