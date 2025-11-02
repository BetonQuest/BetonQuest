package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Catches interactions with MythicMobs and redirect them to BetonQuest Npc-Events.
 */
public class MythicMobsInteractCatcher extends NpcInteractCatcher<ActiveMob> {
    /**
     * Executor for getting MythicMobs.
     */
    private final MobExecutor mobExecutor;

    /**
     * Hider for Mobs.
     */
    private final MythicHider mythicHider;

    /**
     * Initializes the interact catcher.
     *
     * @param profileProvider the profile provider instance
     * @param npcRegistry     the registry to identify the clicked Npc
     * @param mobExecutor     the executor used get MythicMobs
     * @param mythicHider     the hider for mobs
     */
    public MythicMobsInteractCatcher(final ProfileProvider profileProvider, final NpcRegistry npcRegistry,
                                     final MobExecutor mobExecutor, final MythicHider mythicHider) {
        super(profileProvider, npcRegistry);
        this.mobExecutor = mobExecutor;
        this.mythicHider = mythicHider;
    }

    /**
     * Catches a left click.
     *
     * @param event the damage event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeft(final EntityDamageByEntityEvent event) {
        final ActiveMob activeMob = mobExecutor.getMythicMobInstance(event.getEntity());
        if (activeMob != null && event.getDamager() instanceof Player player
                && interactLogic(player, new MythicMobsNpcAdapter(activeMob, mythicHider), Interaction.LEFT,
                event.isCancelled(), event.isAsynchronous())) {
            event.setCancelled(true);
        }
    }

    /**
     * Catches a right click.
     *
     * @param event the interact event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRight(final MythicMobInteractEvent event) {
        if (interactLogic(event.getPlayer(), new MythicMobsNpcAdapter(event.getActiveMob(), mythicHider), Interaction.RIGHT,
                event.isCancelled(), event.isAsynchronous())) {
            event.setCancelled();
        }
    }

    /**
     * Update the NPC holograms when the mob moves.
     *
     * @param event The entity move event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(final EntityMoveEvent event) {
        final ActiveMob activeMob = mobExecutor.getMythicMobInstance(event.getEntity());
        if (activeMob != null) {
            updateHolo(activeMob);
        }
    }

    /**
     * Update the NPC holograms when the mob dies.
     *
     * @param event The mm death event
     */
    @EventHandler
    public void onDeath(final MythicMobDeathEvent event) {
        updateHolo(event.getMob());
    }

    /**
     * Update the NPC holograms when the mob is removed.
     *
     * @param event The mm despawn event
     */
    @EventHandler
    public void onRemove(final MythicMobDespawnEvent event) {
        updateHolo(event.getMob());
    }

    private void updateHolo(final ActiveMob activeMob) {
        new NpcVisibilityUpdateEvent(new MythicMobsNpcAdapter(activeMob, mythicHider)).callEvent();
    }
}
