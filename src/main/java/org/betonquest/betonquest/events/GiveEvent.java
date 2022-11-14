package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

/**
 * Gives the player specified items
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class GiveEvent extends QuestEvent {

    private final Item[] questItems;
    private final boolean notify;

    public GiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItems = instruction.getItemList();
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        for (final Item theItem : questItems) {
            final QuestItem questItem = theItem.getItem();
            final VariableNumber amount = theItem.getAmount();
            int amountInt = amount.getInt(profile);
            if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getPackagePath(), profile.getOnlineProfile().get(), "items_given",
                            new String[]{
                                    questItem.getName() == null ? questItem.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ") : questItem.getName(),
                                    String.valueOf(amountInt)}, "items_given,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'mobs_to_kill' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
            while (amountInt > 0) {
                final int stackSize;
                if (amountInt > 64) {
                    stackSize = 64;
                } else {
                    stackSize = amountInt;
                }
                final ItemStack item = questItem.generate(stackSize, profile);
                final HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
                for (final ItemStack itemStack : left.values()) {
                    if (Utils.isQuestItem(itemStack)) {
                        BetonQuest.getInstance().getPlayerData(profile).addItem(itemStack, stackSize);
                    } else {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }
                    final String type = Utils.isQuestItem(itemStack) ? "backpack" : "drop";
                    try {
                        Config.sendNotify(null, profile.getOnlineProfile().get(), "inventory_full_" + type, null, "inventory_full_" + type + ",inventory_full,error");
                    } catch (final QuestRuntimeException e) {
                        LOG.warn("The notify system was unable to play a sound for the 'inventory_full_" + type + "' category. Error was: '" + e.getMessage() + "'", e);
                    }
                }
                amountInt = amountInt - stackSize;
            }
        }
        return null;
    }
}
