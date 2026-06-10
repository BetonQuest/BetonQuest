package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * Action to add profession experience to a player.
 */
public class MMOCoreProfessionExperienceAction implements OnlineAction {

    /**
     * Profession to add experience to.
     */
    private final Argument<Profession> profession;

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * If level should be added instead of experience.
     */
    private final FlagArgument<Boolean> level;

    /**
     * Create a new class point add action.
     *
     * @param profession the profession to add experience to
     * @param amount     the amount to grant
     * @param level      whether to add level instead of experience
     */
    public MMOCoreProfessionExperienceAction(final Argument<Profession> profession, final Argument<Number> amount, final FlagArgument<Boolean> level) {
        this.profession = profession;
        this.amount = amount;
        this.level = level;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Profession profession = this.profession.getValue(profile);
        final int amount = this.amount.getValue(profile).intValue();
        final PlayerData mmoData = PlayerData.get(profile.getPlayerUUID());

        if (level.getValue(profile).orElse(false)) {
            mmoData.getCollectionSkills().giveLevels(profession, amount, EXPSource.QUEST);
        } else {
            mmoData.getCollectionSkills().giveExperience(profession, amount, EXPSource.QUEST);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
