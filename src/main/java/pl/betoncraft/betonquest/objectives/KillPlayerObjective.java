package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

public class KillPlayerObjective extends Objective implements Listener {

    private final int notifyInterval;
    private int amount = 1;
    private String name = null;
    private final ConditionID[] required;
    private final boolean notify;

    public KillPlayerObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = KillData.class;
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 0");
        }
        name = instruction.getOptional("name");
        required = instruction.getList(instruction.getOptional("required"), e -> instruction.getCondition(e))
                .toArray(new ConditionID[0]);
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 0;
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(final PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        final String victim = PlayerConverter.getID(event.getEntity());
        final String killer = PlayerConverter.getID(event.getEntity().getKiller());
        if (containsPlayer(killer)) {
            if (name != null && !event.getEntity().getName().equalsIgnoreCase(name)) {
                return;
            }
            for (final ConditionID condition : required) {
                if (!BetonQuest.condition(victim, condition)) {
                    return;
                }
            }
            if (!checkConditions(killer)) {
                return;
            }
            final KillData data = (KillData) dataMap.get(killer);
            data.kill();
            if (data.getLeft() <= 0) {
                completeObjective(killer);
            } else if (notify && data.getLeft() % notifyInterval == 0) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), killer, "players_to_kill", new String[]{String.valueOf(data.getLeft())},
                            "players_to_kill,info");
                } catch (final QuestRuntimeException exception) {
                    try {
                        LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'players_to_kill' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                    } catch (final InstructionParseException exep) {
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
        return String.valueOf(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }

    public static class KillData extends ObjectiveData {

        private int amount;

        public KillData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void kill() {
            amount--;
            update();
        }

        public int getLeft() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
