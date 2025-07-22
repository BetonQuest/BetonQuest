package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Player needs to die. Death can be canceled, also respawn location can be set
 */
public class DieObjective extends Objective implements Listener {

    /**
     * Whether the death should be canceled.
     */
    private final boolean cancel;

    /**
     * Location where the player should respawn.
     */
    @Nullable
    private final Variable<Location> location;

    /**
     * Constructor for the DieObjective.
     *
     * @param instruction the instruction that created this objective
     * @param cancel      whether the death should be canceled
     * @param location    the location where the player should respawn
     * @throws QuestException if there is an error in the instruction
     */
    public DieObjective(final Instruction instruction, final boolean cancel, @Nullable final Variable<Location> location) throws QuestException {
        super(instruction);
        this.cancel = cancel;
        this.location = location;
    }

    /**
     * Check if the player died.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final EntityDeathEvent event) {
        if (cancel || location != null) {
            return;
        }
        if (event.getEntity() instanceof final Player player) {
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
            if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        }
    }

    /**
     * Check if the player respawned.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        if (cancel || location == null) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            qeHandler.handle(() -> {
                getLocation(onlineProfile).ifPresent(event::setRespawnLocation);
                completeObjective(onlineProfile);
            });
        }
    }

    /**
     * Check if the player died by damage.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLastDamage(final EntityDamageEvent event) {
        if (!cancel || !(event.getEntity() instanceof final Player player)) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (containsPlayer(onlineProfile) && player.getHealth() - event.getFinalDamage() <= 0
                && checkConditions(onlineProfile)) {
            event.setCancelled(true);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setExhaustion(4);
            player.setSaturation(20);
            for (final PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            qeHandler.handle(() -> {
                final Optional<Location> targetLocation = getLocation(onlineProfile);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        targetLocation.ifPresent(player::teleport);
                        player.setFireTicks(0);
                    }
                }.runTaskLater(BetonQuest.getInstance(), 1);
                completeObjective(onlineProfile);
            });
        }
    }

    private Optional<Location> getLocation(final OnlineProfile onlineProfile) throws QuestException {
        return location == null ? Optional.empty() : Optional.of(location.getValue(onlineProfile));
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
