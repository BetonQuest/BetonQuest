package org.betonquest.betonquest.compatibility.itemsadder.action;

import dev.lone.itemsadder.api.ItemsAdder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * Plays an ItemsAdder animation to a player.
 */
public class IAPlayAnimationAction implements OnlineAction {

    /**
     * Animation name.
     */
    private final Argument<String> name;

    /**
     * Create a new ItemsAdder animation action.
     *
     * @param animation the name of the animation
     */
    public IAPlayAnimationAction(final Argument<String> animation) {
        this.name = animation;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        ItemsAdder.playTotemAnimation(profile.getPlayer(), name.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
