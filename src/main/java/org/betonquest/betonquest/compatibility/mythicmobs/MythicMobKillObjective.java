package org.betonquest.betonquest.compatibility.mythicmobs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Player has to kill MythicMobs monster.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class MythicMobKillObjective extends CountingObjective implements Listener {

    private final Set<String> names = new HashSet<>();
    private final double neutralDeathRadiusAllPlayers;
    private final double neutralDeathRadiusAllPlayersSquared;
    private final VariableNumber minMobLevel;
    private final VariableNumber maxMobLevel;

    public MythicMobKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "mobs_to_kill");

        Collections.addAll(names, instruction.getArray());
        targetAmount = instruction.getInt(instruction.getOptional("amount"), 1);

        neutralDeathRadiusAllPlayers = instruction.getDouble(instruction.getOptional("neutralDeathRadiusAllPlayers"), 0);
        neutralDeathRadiusAllPlayersSquared = neutralDeathRadiusAllPlayers * neutralDeathRadiusAllPlayers;

        final String unsafeMinMobLevel = instruction.getOptional("minLevel");
        final String unsafeMaxMobLevel = instruction.getOptional("maxLevel");
        final String packName = instruction.getPackage().getPackagePath();

        minMobLevel = unsafeMinMobLevel == null ? new VariableNumber(Double.NEGATIVE_INFINITY) : new VariableNumber(packName, unsafeMinMobLevel);
        maxMobLevel = unsafeMaxMobLevel == null ? new VariableNumber(Double.POSITIVE_INFINITY) : new VariableNumber(packName, unsafeMaxMobLevel);
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onBossKill(final MythicMobDeathEvent event) {
        if (!names.contains(event.getMobType().getInternalName())) {
            return;
        }

        if (event.getKiller() instanceof Player) {
            handlePlayerKill((Player) event.getKiller(), event.getMob());
        } else {
            if (neutralDeathRadiusAllPlayers > 0) {
                final Location center = BukkitAdapter.adapt(event.getMob().getLocation());
                for (final Player player : center.getWorld().getPlayers()) {
                    if (isValidPlayer(player)
                            && player.getLocation().distanceSquared(center) <= neutralDeathRadiusAllPlayersSquared) {
                        handlePlayerKill(player, event.getMob());
                    }
                }
            }
        }
    }

    private boolean isValidPlayer(final Player player) {
        return player != null
                && player.isOnline()
                && player.isValid();
    }

    private void handlePlayerKill(final Player player, final ActiveMob mob) {
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID)
                && matchesMobLevel(playerID, mob)
                && checkConditions(playerID)) {
            getCountingData(playerID).progress();
            completeIfDoneOrNotify(playerID);
        }

    }

    private boolean matchesMobLevel(final String playerID, final ActiveMob mob) {
        try {
            final double actualMobLevel = mob.getLevel();
            return minMobLevel.getDouble(playerID) <= actualMobLevel && maxMobLevel.getDouble(playerID) >= actualMobLevel;
        } catch (final QuestRuntimeException exception) {
            try {
                LOG.error(instruction.getPackage(), "Unable to resolve minMobLevel / maxMobLevel variable in " + instruction.getObjective().getFullID());
            } catch (final InstructionParseException e) {
                LOG.reportException(instruction.getPackage(), exception);
            }
            return false;
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
}
