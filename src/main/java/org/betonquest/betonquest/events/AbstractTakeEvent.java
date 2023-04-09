package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Removes items from player's inventory and/or backpack
 *
 * @deprecated Only needed because the 1.12.X item system cannot work with special items like the ones from MMOItems.
 * Will be removed in 2.0 in favor of the new item system. Then the {@link TakeEvent} will be able to handle everything.
 */
@Deprecated
@SuppressWarnings("PMD.CommentRequired")
public abstract class AbstractTakeEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(AbstractTakeEvent.class);

    protected final boolean notify;
    protected final List<CheckType> checkOrder;

    public AbstractTakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        checkOrder = getCheckOrder(instruction);
        notify = instruction.hasArgument("notify");
    }

    private List<CheckType> getCheckOrder(final Instruction instruction) throws InstructionParseException {
        final String order = instruction.getOptional("invOrder");
        if (order == null) {
            return Arrays.asList(CheckType.INVENTORY, CheckType.OFFHAND, CheckType.ARMOR, CheckType.BACKPACK);
        } else {
            final String[] enumNames = order.split(",");
            final ArrayList<CheckType> checkOrder = new ArrayList<>();
            for (final String s : enumNames) {
                try {
                    final CheckType checkType = CheckType.valueOf(s.toUpperCase(Locale.ROOT));
                    checkOrder.add(checkType);
                } catch (final IllegalArgumentException e) {
                    throw new InstructionParseException("There is no such check type: " + s, e);
                }
            }
            return checkOrder;
        }
    }

    protected void checkSelectedTypes(final Player player) {
        for (final CheckType type : checkOrder) {
            switch (type) {
                case INVENTORY:
                    checkInventory(player);
                    break;
                case ARMOR:
                    checkArmor(player);
                    break;
                case OFFHAND:
                    checkOffhand(player);
                    break;
                case BACKPACK:
                    checkBackpack(PlayerConverter.getID(player));
                    break;
            }
        }
    }

    protected void notifyPlayer(final OnlineProfile onlineProfile, final String itemName, final int amount) {
        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getQuestPath(), onlineProfile, "items_taken",
                        new String[]{itemName, String.valueOf(amount)}, "items_taken,info");
            } catch (final QuestRuntimeException exception) {
                LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'items_taken' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'", exception);
            }
        }
    }

    protected void checkInventory(final Player player) {
        final ItemStack[] inventory = player.getInventory().getStorageContents();
        final ItemStack[] newInv = takeDesiredAmount(PlayerConverter.getID((OfflinePlayer) player), inventory);
        player.getInventory().setStorageContents(newInv);
    }

    protected void checkArmor(final Player player) {
        final ItemStack[] armorSlots = player.getInventory().getArmorContents();
        final ItemStack[] newArmor = takeDesiredAmount(PlayerConverter.getID((OfflinePlayer) player), armorSlots);
        player.getInventory().setArmorContents(newArmor);
    }

    protected void checkOffhand(final Player player) {
        final ItemStack offhand = player.getInventory().getItemInOffHand();
        final ItemStack[] newOffhand = takeDesiredAmount(PlayerConverter.getID((OfflinePlayer) player), offhand);
        player.getInventory().setItemInOffHand(newOffhand[0]);
    }

    protected void checkBackpack(final Profile profile) {
        final List<ItemStack> backpack = BetonQuest.getInstance().getPlayerData(profile).getBackpack();
        final List<ItemStack> newBackpack = removeDesiredAmount(profile, backpack);
        BetonQuest.getInstance().getPlayerData(profile).setBackpack(newBackpack);
    }

    protected List<ItemStack> removeDesiredAmount(final Profile profile, final List<ItemStack> items) {
        final ItemStack[] itemArray = items.toArray(new ItemStack[0]);
        final ItemStack[] remainingItems = takeDesiredAmount(profile, itemArray);
        return Arrays.stream(remainingItems)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    protected abstract ItemStack[] takeDesiredAmount(Profile profile, ItemStack... items);

    protected enum CheckType {
        INVENTORY,
        ARMOR,
        OFFHAND,
        BACKPACK
    }
}
