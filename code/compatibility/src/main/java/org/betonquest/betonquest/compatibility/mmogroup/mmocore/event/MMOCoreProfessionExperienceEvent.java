package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to add profession experience to a player.
 */
public class MMOCoreProfessionExperienceEvent implements PlayerEvent {

    /**
     * Profession to add experience to.
     */
    private final Variable<Profession> profession;

    /**
     * Amount to grant.
     */
    private final Variable<Number> amountVar;

    /**
     * If level should be added instead of experience.
     */
    private final boolean isLevel;

    /**
     * Create a new class point add event.
     *
     * @param profession the profession to add experience to
     * @param amount     the amount to grant
     * @param isLevel    whether to add level instead of experience
     */
    public MMOCoreProfessionExperienceEvent(final Variable<Profession> profession, final Variable<Number> amount, final boolean isLevel) {
        this.profession = profession;
        this.amountVar = amount;
        this.isLevel = isLevel;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Profession profession = this.profession.getValue(profile);
        final int amount = amountVar.getValue(profile).intValue();
        final PlayerData mmoData = PlayerData.get(profile.getPlayerUUID());

        if (isLevel) {
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
