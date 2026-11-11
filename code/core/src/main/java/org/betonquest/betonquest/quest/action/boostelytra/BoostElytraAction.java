package org.betonquest.betonquest.quest.action.boostelytra;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Boosts the player's elytra.
 */
public class BoostElytraAction implements OnlineAction {

    /**
     * The default boost level.
     */
    private static final int DEFAULT_BOOST_LEVEL = 1;

    /**
     * The item to use for boosting.
     */
    @Nullable
    private final Argument<ItemWrapper> boostItem;

    /**
     * The boost level.
     */
    @Nullable
    private final Argument<Number> boostLevel;

    /**
     * Creates a new BoostElytraAction.
     *
     * @param boostItem  the item to use for boosting
     * @param boostLevel the boost level
     */
    public BoostElytraAction(@Nullable final Argument<ItemWrapper> boostItem, @Nullable final Argument<Number> boostLevel) {
        this.boostItem = boostItem;
        this.boostLevel = boostLevel;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        if (!player.isGliding()) {
            return;
        }
        player.boostElytra(createBooster(profile));
    }

    /**
     * Creates a booster item from the given arguments.
     *
     * @param profile the profile to use for generating the item
     * @return the created booster item
     * @throws QuestException if the booster item is invalid
     */
    protected ItemStack createBooster(final Profile profile) throws QuestException {
        final ItemStack booster = boostItem == null ? new ItemStack(Material.FIREWORK_ROCKET) : boostItem.getValue(profile).generate(profile);
        if (booster.getType() != Material.FIREWORK_ROCKET) {
            throw new QuestException("Invalid booster item type: " + booster.getType());
        }
        if (boostLevel == null && boostItem != null) {
            return booster;
        }
        final int powerLevel = boostLevel == null ? DEFAULT_BOOST_LEVEL : boostLevel.getValue(profile).intValue();
        booster.editMeta(FireworkMeta.class, fireworkMeta -> fireworkMeta.setPower(powerLevel));
        return booster;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
