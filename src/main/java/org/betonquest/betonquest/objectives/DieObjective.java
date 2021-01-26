package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Player needs to die. Death can be canceled, also respawn location can be set
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class DieObjective extends Objective implements Listener {

    private final boolean cancel;
    private final CompoundLocation location;

    public DieObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        cancel = instruction.hasArgument("cancel");
        location = instruction.getLocation(instruction.getOptional("respawn"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final EntityDeathEvent event) {
        if (cancel) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            final String playerID = PlayerConverter.getID((Player) event.getEntity());
            if (containsPlayer(playerID) && checkConditions(playerID)) {
                completeObjective(playerID);
            }
        }
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLastDamage(final EntityDamageEvent event) {
        if (!cancel || !(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getEntity();
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID) && player.getHealth() - event.getFinalDamage() <= 0
                && checkConditions(playerID)) {
            event.setCancelled(true);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setExhaustion(4);
            player.setSaturation(20);
            for (final PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            Location targetLocation = null;
            try {
                if (location != null) {
                    targetLocation = location.getLocation(playerID);
                }
            } catch (final QuestRuntimeException e) {
                LOG.warning(instruction.getPackage(), "Couldn't execute onLastDamage in DieObjective", e);
            }
            final Location finaltagetLocation = targetLocation;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (finaltagetLocation != null) {
                        player.teleport(finaltagetLocation);
                    }
                    player.setFireTicks(0);

                }
            }.runTaskLater(BetonQuest.getInstance(), 1);
            completeObjective(playerID);
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
