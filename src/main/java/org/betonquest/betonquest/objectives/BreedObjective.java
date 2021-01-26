package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class BreedObjective extends Objective implements Listener {

    private final EntityType type;
    private final int amount;
    private final boolean notify;

    public BreedObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = BreedData.class;
        type = instruction.getEntity();
        amount = instruction.getPositive();
        notify = instruction.hasArgument("notify");
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getEntityType() != type || !(event.getBreeder() instanceof Player)) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getBreeder());
        if (!containsPlayer(playerID)) {
            return;
        }
        if (checkConditions(playerID)) {
            final BreedData data = (BreedData) dataMap.get(playerID);
            data.breed();
            if (data.getAmount() == 0) {
                completeObjective(playerID);
            } else if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "animals_to_breed", new String[]{String.valueOf(data.getAmount())},
                            "animals_to_breed,info");
                } catch (final QuestRuntimeException exception) {
                    try {
                        LOG.warning("The notify system was unable to play a sound for the 'animals_to_breed' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                    } catch (final InstructionParseException e) {
                        LOG.reportException(e);
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
        switch (name.toLowerCase(Locale.ROOT)) {
            case "left":
                return Integer.toString(((BreedData) dataMap.get(playerID)).getAmount());
            case "amount":
                return Integer.toString(amount);
            default:
                return "";
        }
    }

    public static class BreedData extends ObjectiveData {

        private int amount;

        public BreedData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void breed() {
            amount--;
            update();
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
