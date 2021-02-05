package org.betonquest.betonquest.objectives;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class JumpObjective extends Objective implements Listener {

    private final int amount;

    public JumpObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = JumpData.class;

        amount = instruction.getInt();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(final PlayerJumpEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            final JumpData playerData = (JumpData) dataMap.get(playerID);
            playerData.subtract();
            if (playerData.isZero()) {
                completeObjective(playerID);
            }
        }
    }


    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(((JumpData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(amount);
        }
        return "";
    }


    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    public static class JumpData extends ObjectiveData {

        private int amount;

        public JumpData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private int getAmount() {
            return amount;
        }

        private void subtract() {
            this.amount--;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

    }
}
