package org.betonquest.betonquest.compatibility.itemsadder.action;

import dev.lone.itemsadder.api.ItemsAdder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;

/**
 * An action that plays a specific ItemsAdder totem animation for an online player.
 *
 * <p>This action retrieves the animation name from the provided argument and
 * triggers the animation using the ItemsAdder API for the player associated
 * with the given profile.</p>
 */
public class ItemsAdderPlayAnimationAction implements OnlineAction {

    /**
     * The name of the animation to play.
     */
    private final Argument<String> animationArgument;

    /**
     * Creates a new ItemsAdderPlayAnimationAction.
     *
     * @param animationArgument the argument for the animation name
     */
    public ItemsAdderPlayAnimationAction(final Argument<String> animationArgument) {
        this.animationArgument = animationArgument;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        ItemsAdder.playTotemAnimation(profile.getPlayer(), animationArgument.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
