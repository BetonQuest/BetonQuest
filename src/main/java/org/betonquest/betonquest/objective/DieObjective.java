package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.util.PlayerConverter;
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
@SuppressWarnings("PMD.CommentRequired")
public class DieObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final boolean cancel;

    @Nullable
    private final VariableLocation location;

    public DieObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        cancel = instruction.hasArgument("cancel");
        location = instruction.get(instruction.getOptional("respawn"), VariableLocation::new);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final EntityDeathEvent event) {
        if (cancel || location != null) {
            return;
        }
        if (event.getEntity() instanceof final Player player) {
            final OnlineProfile onlineProfile = PlayerConverter.getID(player);
            if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        if (cancel || location == null) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            getLocation(onlineProfile).ifPresent(event::setRespawnLocation);
            completeObjective(onlineProfile);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLastDamage(final EntityDamageEvent event) {
        if (!cancel || !(event.getEntity() instanceof final Player player)) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
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

    private Optional<Location> getLocation(final OnlineProfile onlineProfile) {
        if (location == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(location.getValue(onlineProfile));
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
            return Optional.empty();
        }
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
