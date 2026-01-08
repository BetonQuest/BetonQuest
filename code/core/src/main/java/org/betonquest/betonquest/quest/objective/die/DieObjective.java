package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
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
public class DieObjective extends DefaultObjective {

    /**
     * Whether the death should be canceled.
     */
    private final FlagArgument<Boolean> cancel;

    /**
     * Location where the player should respawn.
     */
    @Nullable
    private final Argument<Location> location;

    /**
     * Constructor for the DieObjective.
     *
     * @param service  the objective factory service
     * @param cancel   whether the death should be canceled
     * @param location the location where the player should respawn
     * @throws QuestException if there is an error in the instruction
     */
    public DieObjective(final ObjectiveFactoryService service, final FlagArgument<Boolean> cancel, @Nullable final Argument<Location> location) throws QuestException {
        super(service);
        this.cancel = cancel;
        this.location = location;
    }

    /**
     * Check if the player died.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile related to the player that died
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onDeath(final EntityDeathEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (location != null) {
            return;
        }
        if (cancel.getValue(onlineProfile).orElse(false)) {
            return;
        }
        completeObjective(onlineProfile);
    }

    /**
     * Check if the player respawned.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile related to the player that respawned
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onRespawn(final PlayerRespawnEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (cancel.getValue(onlineProfile).orElse(false) || location == null) {
            return;
        }
        getLocation(onlineProfile).ifPresent(event::setRespawnLocation);
        completeObjective(onlineProfile);
    }

    /**
     * Check if the player died by damage.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile last damaged by the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onLastDamage(final EntityDamageEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final Player player = onlineProfile.getPlayer();
        if (!cancel.getValue(onlineProfile).orElse(false)) {
            return;
        }
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setExhaustion(4);
            player.setSaturation(20);
            for (final PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            final Optional<Location> targetLocation = getLocation(onlineProfile);
            new BukkitRunnable() {
                @Override
                public void run() {
                    targetLocation.ifPresent(player::teleport);
                    player.setFireTicks(0);
                }
            }.runTaskLater(BetonQuest.getInstance(), 1);
            completeObjective(onlineProfile);
        }
    }

    private Optional<Location> getLocation(final OnlineProfile onlineProfile) throws QuestException {
        return location == null ? Optional.empty() : Optional.of(location.getValue(onlineProfile));
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
