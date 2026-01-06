package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Event to add class experience to a player.
 */
public class MMOCoreClassExperienceAction implements PlayerAction {

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * If level should be added instead of experience.
     */
    private final FlagArgument<Boolean> level;

    /**
     * Create a new experience add event.
     *
     * @param amount the amount to grant
     * @param level  whether to add level instead of experience
     */
    public MMOCoreClassExperienceAction(final Argument<Number> amount, final FlagArgument<Boolean> level) {
        this.amount = amount;
        this.level = level;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final int amount = this.amount.getValue(profile).intValue();
        final PlayerData mmoData = PlayerData.get(profile.getPlayerUUID());

        if (level.getValue(profile).orElse(false)) {
            mmoData.giveLevels(amount, EXPSource.QUEST);
        } else {
            mmoData.giveExperience(amount, EXPSource.QUEST);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
