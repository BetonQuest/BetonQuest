package pl.betoncraft.betonquest.compatibility.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
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
public class MythicMobKillObjective extends Objective implements Listener {

    private final Set<String> names = new HashSet<>();
    private final int amount;
    private final boolean notify;
    private final VariableNumber minMobLevel;
    private final VariableNumber maxMobLevel;

    public MythicMobKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = MMData.class;
        notify = instruction.hasArgument("notify");

        Collections.addAll(names, instruction.getArray());
        amount = instruction.getInt(instruction.getOptional("amount"), 1);

        final String unsafeMinMobLevel = instruction.getOptional("minLevel");
        final String unsafeMaxMobLevel = instruction.getOptional("maxLevel");
        final String packName = instruction.getPackage().getName();

        minMobLevel = unsafeMinMobLevel == null ? new VariableNumber(Double.NEGATIVE_INFINITY) : new VariableNumber(packName, unsafeMinMobLevel);
        maxMobLevel = unsafeMaxMobLevel == null ? new VariableNumber(Double.POSITIVE_INFINITY) : new VariableNumber(packName, unsafeMaxMobLevel);
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @EventHandler(ignoreCancelled = true)
    public void onBossKill(final MythicMobDeathEvent event) {
        if (!names.contains(event.getMobType().getInternalName())) {
            return;
        }
        if (!(event.getKiller() instanceof Player)) {
            return;
        }

        final String playerID = PlayerConverter.getID((Player) event.getKiller());
        if (!containsPlayer(playerID)) {
            return;
        }

        final double actualMobLevel = event.getMobLevel();
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
        } else if (notify) {
            // send a notification
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "mobs_to_kill", new String[]{String.valueOf(playerData.getAmount())}, "mobs_to_kill,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'mobs_to_kill' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException exep) {
                    try {
                        throw new QuestRuntimeException(exep);
                    } catch (QuestRuntimeException e) {
                        LogUtils.logThrowableReport(exep);
                    }
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
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(((MMData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
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
