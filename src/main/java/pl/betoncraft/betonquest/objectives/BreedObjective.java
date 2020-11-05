package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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

    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getEntityType() == type && event.getBreeder() instanceof Player) {
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
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "animals_to_breed", new String[]{String.valueOf(data.getAmount())},
                            "animals_to_breed,info");
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
            return Integer.toString(((BreedData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(amount);
        }
        return "";
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
