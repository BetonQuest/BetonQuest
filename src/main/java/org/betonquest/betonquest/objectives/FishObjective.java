package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Requires the player to catch the fish.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class FishObjective extends Objective implements Listener {

    private final int amount;
    private final boolean notify;
    private final int notifyInterval;
    private final BlockSelector blockSelector;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public FishObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = FishData.class;
        blockSelector = new BlockSelector(instruction.next());
        amount = instruction.getInt();
        if (amount <= 0) {
            throw new InstructionParseException("Fish amount cannot be less than 0");
        }
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @SuppressWarnings({"deprecation", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onFishCatch(final PlayerFishEvent event) {
        if (event.getState() != State.CAUGHT_FISH) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        if (event.getCaught() == null) {
            return;
        }
        if (event.getCaught().getType() != EntityType.DROPPED_ITEM) {
            return;
        }
        final ItemStack item = ((Item) event.getCaught()).getItemStack();
        if (!blockSelector.match(item.getType())) {
            return;
        }
        final FishData data = (FishData) dataMap.get(playerID);
        if (checkConditions(playerID)) {
            data.catchFish();
        }
        if (data.getAmount() <= 0) {
            completeObjective(playerID);
        } else if (notify && data.getAmount() % notifyInterval == 0) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "fish_to_catch", new String[]{String.valueOf(data.getAmount())},
                        "fish_to_catch,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'fish_to_catch' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException e) {
                    LOG.reportException(instruction.getPackage(), e);
                }
            }
        }
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "left":
                return Integer.toString(((FishData) dataMap.get(playerID)).getAmount());
            case "amount":
                return Integer.toString(amount);
            default:
                return "";
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

    public static class FishData extends ObjectiveData {

        private int amount;

        public FishData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void catchFish() {
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
