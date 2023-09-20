package pl.betoncraft.betonquest.compatibility.mythicmobs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Player has to kill MythicMobs monster
 */
@SuppressWarnings("PMD.CommentRequired")
public class MythicMobKillObjective extends Objective implements Listener {

    private final Set<String> names = new HashSet<>();
    private final int amount;
    private final int notifyInterval;
    private final boolean notify;
    private final double neutralDeathRadiusAllPlayers;
    private final double neutralDeathRadiusAllPlayersSquared;
    private final VariableNumber minMobLevel;
    private final VariableNumber maxMobLevel;

    public MythicMobKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = MMData.class;
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;

        Collections.addAll(names, instruction.getArray());
        amount = instruction.getInt(instruction.getOptional("amount"), 1);

        neutralDeathRadiusAllPlayers = instruction.getDouble(instruction.getOptional("neutralDeathRadiusAllPlayers"), 0);
        neutralDeathRadiusAllPlayersSquared = neutralDeathRadiusAllPlayers * neutralDeathRadiusAllPlayers;

        final String unsafeMinMobLevel = instruction.getOptional("minLevel");
        final String unsafeMaxMobLevel = instruction.getOptional("maxLevel");
        final String packName = instruction.getPackage().getName();

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
                    if (!isValidPlayer(player)) {
                        continue;
                    }

                    if (player.getLocation().distanceSquared(center) > neutralDeathRadiusAllPlayersSquared) {
                        continue;
                    }

                    handlePlayerKill(player, event.getMob());
                }
            }
        }
    }

    private boolean isValidPlayer(final Player player) {
        if (player == null) {
            return false;
        }

        if (!player.isOnline()) {
            return false;
        }

        return player.isValid();
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    private void handlePlayerKill(final Player player, final ActiveMob mob) {
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID)) {
            return;
        }

        final double actualMobLevel = mob.getLevel();
        try {
            if (minMobLevel.getDouble(playerID) > actualMobLevel || maxMobLevel.getDouble(playerID) < actualMobLevel) {
                return;
            }
        } catch (QuestRuntimeException exep) {
            try {
                LogUtils.getLogger().log(Level.SEVERE, "Unable to resolve minMobLevel / maxMobLevel variable in " + instruction.getObjective().getFullID());
            } catch (InstructionParseException e) {
                LogUtils.logThrowableReport(exep);
            }
            return;
        }

        if (!checkConditions(playerID)) {
            return;
        }

        final MMData playerData = (MMData) dataMap.get(playerID);
        playerData.kill();

        if (playerData.killed()) {
            completeObjective(playerID);
        } else if (notify && playerData.getAmount() % notifyInterval == 0) {
            // send a notification
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "mobs_to_kill", new String[]{String.valueOf(playerData.getAmount())}, "mobs_to_kill,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'mobs_to_kill' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException exep) {
                    LogUtils.logThrowableReport(exep);
                }
            }
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
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(((MMData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((MMData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class MMData extends ObjectiveData {

        private int amount;

        public MMData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void kill() {
            amount--;
            update();
        }

        private boolean killed() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
