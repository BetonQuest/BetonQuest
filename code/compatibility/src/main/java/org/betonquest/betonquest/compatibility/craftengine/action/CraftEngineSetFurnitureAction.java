package org.betonquest.betonquest.compatibility.craftengine.action;

import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.core.entity.furniture.CustomFurniture;
import net.momirealms.craftengine.core.item.CustomItem;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * An action that places a CraftEngine custom furniture at a specific location.
 *
 * <p>This action resolves the custom item and location from the profile,
 * validates that the item is a valid furniture type in CraftEngine,
 * and then places it at the target location.</p>
 */
public class CraftEngineSetFurnitureAction implements PlayerAction {

    /**
     * The custom item to be placed as furniture.
     */
    private final Argument<CustomItem<ItemStack>> customItem;

    /**
     * The target location for the furniture placement.
     */
    private final Argument<Location> location;

    /**
     * Whether to play the placement sound.
     */
    private final Argument<Boolean> playSound;

    /**
     * Creates a new CraftEngineSetFurnitureAction.
     *
     * @param customItem the argument for the custom item to place
     * @param location   the argument for the target location
     * @param playSound  the argument for playing the placement sound
     */
    public CraftEngineSetFurnitureAction(
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

        final CustomFurniture customFurniture = CraftEngineFurniture.byId(stackCustomItem.id());
        if (customFurniture == null) {
            throw new QuestException("CraftEngine item is not a furniture: " + stackCustomItem.id());
        }

        CraftEngineFurniture.place(loc, stackCustomItem.id(), customFurniture.anyVariantName(), playSound.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }

}
