package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Citizens Compatibility Adapter for BetonQuest Npcs.
 */
public class CitizensAdapter implements Npc<NPC> {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The Citizens NPC instance.
     */
    private final NPC npc;

    /**
     * Create a new Citizens Npc Adapter.
     *
     * @param plugin the plugin instance
     * @param npc    the Citizens NPC instance
     */
    public CitizensAdapter(final Plugin plugin, final NPC npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    @Override
    public NPC getOriginal() {
        return npc;
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public String getFormattedName() {
        return npc.getFullName();
    }

    @Override
    public Optional<Location> getLocation() {
        final Entity entity = npc.getEntity();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(entity.getLocation());
    }

    @Override
    public Optional<Location> getEyeLocation() {
        final Entity entity = npc.getEntity();
        if (entity instanceof LivingEntity) {
            return Optional.of(((LivingEntity) entity).getEyeLocation());
        }
        return getLocation();
    }

    @Override
    public void teleport(final Location location) {
        CitizensIntegrator.getCitizensMoveInstance().stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned()) {
            npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @Override
    public boolean isSpawned() {
        return npc.isSpawned();
    }

    @Override
    public void spawn(final Location location) {
        if (!npc.isSpawned()) {
            npc.spawn(location, SpawnReason.PLUGIN);
        }
    }

    @Override
    public void despawn() {
        if (npc.isSpawned()) {
            npc.despawn();
        }
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        getEntityList(npc).forEach(entity -> onlineProfile.getPlayer().showEntity(plugin, entity));
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        getEntityList(npc).forEach(entity -> onlineProfile.getPlayer().hideEntity(plugin, entity));
    }

    private List<Entity> getEntityList(final NPC npc) {
        final List<Entity> entityList = new ArrayList<>();
        entityList.add(npc.getEntity());

        final HologramTrait hologramTrait = npc.getTraitNullable(HologramTrait.class);
        if (hologramTrait != null) {
            final Entity nameEntity = hologramTrait.getNameEntity();
            if (nameEntity != null) {
                entityList.add(nameEntity);
            }
            entityList.addAll(hologramTrait.getHologramEntities());
        }

        return entityList;
    }
}
