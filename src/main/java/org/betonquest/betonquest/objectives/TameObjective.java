package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * The player must tame specified amount of specified mobs
 */
@SuppressWarnings("PMD.CommentRequired")
public class TameObjective extends Objective implements Listener {

    private final EntityType type;
    private final int amount;

    public TameObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        template = TameData.class;
        type = instruction.getEntity();
        if (type.getEntityClass() == null || !Tameable.class.isAssignableFrom(type.getEntityClass())) {
            throw new InstructionParseException("Entity cannot be tamed: " + type.toString());
        }

        amount = instruction.getInt();
        if (amount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTaming(final EntityTameEvent event) {
        if (event.getOwner() instanceof Player) {
            final String playerID = PlayerConverter.getID((Player) event.getOwner());
            if (!dataMap.containsKey(playerID)) {
                return;
            }
            final LivingEntity entity = event.getEntity();
            final TameData playerData = (TameData) dataMap.get(playerID);

            if (type.equals(entity.getType()) && checkConditions(playerID)) {
                playerData.subtract();
            }

            if (playerData.isZero()) {
                completeObjective(playerID);
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
            return Integer.toString(amount - ((TameData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((TameData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class TameData extends ObjectiveData {

        private int amount;

        public TameData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

        private int getAmount() {
            return amount;
        }

        private void subtract() {
            amount--;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

    }

}
