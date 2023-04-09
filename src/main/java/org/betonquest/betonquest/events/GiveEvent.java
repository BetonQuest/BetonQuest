package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
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
public class GiveEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(GiveEvent.class);

    private final Item[] questItems;
    private final boolean notify;
    private final boolean backpack;

    public GiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItems = instruction.getItemList();
        notify = instruction.hasArgument("notify");
        backpack = instruction.hasArgument("backpack");
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        for (final Item theItem : questItems) {
            final QuestItem questItem = theItem.getItem();
            final VariableNumber amount = theItem.getAmount();
            int amountInt = amount.getInt(profile);
            if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "items_given",
                            new String[]{
                                    questItem.getName() == null ? questItem.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ") : questItem.getName(),
                                    String.valueOf(amountInt)}, "items_given,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'mobs_to_kill' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
            while (amountInt > 0) {
                final int stackSize = Math.min(amountInt, 64);
                final ItemStack item = questItem.generate(stackSize, profile);
                if (backpack && addToBackpack(profile, item)) {
                    amountInt -= stackSize;
                    continue;
                }
                final HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
                for (final ItemStack itemStack : left.values()) {
                    if (!backpack && addToBackpack(profile, itemStack)) {
                        notifyPlayer(profile, NotifyType.BACKPACK);
                    } else {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                        notifyPlayer(profile, NotifyType.DROP);
                    }
                }
                amountInt -= stackSize;
            }
        }
        return null;
    }

    private boolean addToBackpack(final Profile profile, final ItemStack itemStack) {
        if (Utils.isQuestItem(itemStack)) {
            BetonQuest.getInstance().getPlayerData(profile).addItem(itemStack, itemStack.getAmount());
            return true;
        }
        return false;
    }

    private void notifyPlayer(final Profile profile, final NotifyType type) {
        try {
            Config.sendNotify(null, profile.getOnlineProfile().get(), "inventory_full_" + type.toStringLowercase(), null, "inventory_full_" + type.toStringLowercase() + ",inventory_full,error");
        } catch (final QuestRuntimeException e) {
            LOG.warn("The notify system was unable to play a sound for the 'inventory_full_" + type.toStringLowercase() + "' category. Error was: '" + e.getMessage() + "'", e);
        }
    }

    private enum NotifyType {
        BACKPACK, DROP;

        public String toStringLowercase() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }
}
