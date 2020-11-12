package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Gives the player specified items
 */
public class GiveEvent extends QuestEvent {

    private final Item[] questItems;
    private final boolean notify;

    public GiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItems = instruction.getItemList();
        notify = instruction.hasArgument("notify");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        for (final Item theItem : questItems) {
            final QuestItem questItem = theItem.getItem();
            final VariableNumber amount = theItem.getAmount();
            int amountInt = amount.getInt(playerID);
            if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "items_given",
                            new String[]{
                                    questItem.getName() == null ? questItem.getMaterial().toString().toLowerCase().replace("_", " ") : questItem.getName(),
                                    String.valueOf(amountInt)}, "items_given,info");
                } catch (final QuestRuntimeException exception) {
                    try {
                        LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'mobs_to_kill' category in '" + instruction.getEvent().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                    } catch (final InstructionParseException exep) {
                        throw new QuestRuntimeException(exep);
                    }
                }
            }
            while (amountInt > 0) {
                final int stackSize;
                if (amountInt > 64) {
                    stackSize = 64;
                } else {
                    stackSize = amountInt;
                }
                final ItemStack item = questItem.generate(stackSize);
                final HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
                for (final Integer leftNumber : left.keySet()) {
                    final ItemStack itemStack = left.get(leftNumber);
                    if (Utils.isQuestItem(itemStack)) {
                        BetonQuest.getInstance().getPlayerData(playerID).addItem(itemStack, stackSize);
                    } else {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }
                }
                amountInt = amountInt - stackSize;
            }
        }
        return null;
    }
}
