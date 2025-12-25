package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if the player has a level in a profession.
 */
public class MMOCoreProfessionLevelCondition implements PlayerCondition {

    /**
     * Profession name.
     */
    private final Argument<Profession> profession;

    /**
     * If the actual must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Required level.
     */
    private final Argument<Number> targetLevelVar;

    /**
     * Create a new attribute condition.
     *
     * @param profession  the profession to check
     * @param targetLevel the required level
     * @param equal       whether the actual must be equal to the target level
     */
    public MMOCoreProfessionLevelCondition(final Argument<Profession> profession, final Argument<Number> targetLevel, final boolean equal) {
        this.profession = profession;
        this.targetLevelVar = targetLevel;
        this.mustBeEqual = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int actualLevel = data.getCollectionSkills().getLevel(profession.getValue(profile));
        final int targetLevel = targetLevelVar.getValue(profile).intValue();

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
