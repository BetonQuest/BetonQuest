package org.betonquest.betonquest.compatibility.craftengine.action;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.item.CustomItem;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * An action that places a CraftEngine custom block at a specific location.
 * * <p>This action validates if the provided custom item is a valid block within
 * CraftEngine and then places it at the resolved location, optionally playing
 * the placement sound.</p>
 */
public class CraftEngineSetBlockAction implements PlayerAction {

    /**
     * The custom item to be placed as a block.
     */
    private final Argument<CustomItem<ItemStack>> customItem;

    /**
     * The target location for the block placement.
     */
    private final Argument<Location> location;

    /**
     * Whether to play the block placement sound.
     */
    private final Argument<Boolean> playSound;

    /**
     * Creates a new CraftEngineSetBlockAction.
     *
     * @param customItem the argument for the custom item to place
     * @param location   the argument for the target location
     * @param playSound  the argument for playing the placement sound
     */
    public CraftEngineSetBlockAction(
            final Argument<CustomItem<ItemStack>> customItem,
            final Argument<Location> location,
            final Argument<Boolean> playSound
    ) {
        this.customItem = customItem;
        this.location = location;
        this.playSound = playSound;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Location loc = location.getValue(profile);
        final CustomItem<ItemStack> stackCustomItem = customItem.getValue(profile);

        if (CraftEngineBlocks.byId(stackCustomItem.id()) == null) {
            throw new QuestException("CraftEngine item is not a block: " + stackCustomItem.id());
        }

        CraftEngineBlocks.place(loc, stackCustomItem.id(), playSound.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }

}
