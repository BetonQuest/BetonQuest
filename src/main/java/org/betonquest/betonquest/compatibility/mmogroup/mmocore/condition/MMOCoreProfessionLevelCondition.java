package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Condition to check if the player has a level in a profession.
 */
public class MMOCoreProfessionLevelCondition implements PlayerCondition {
    /**
     * Profession name.
     */
    private final String professionName;

    /**
     * Required level.
     */
    private final VariableNumber targetLevelVar;

    /**
     * If the actual must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Create a new attribute condition.
     *
     * @param profession  the profession to check
     * @param targetLevel the required level
     * @param equal       whether the actual must be equal to the target level
     */
    public MMOCoreProfessionLevelCondition(final String profession, final VariableNumber targetLevel, final boolean equal) {
        this.professionName = profession;
        this.targetLevelVar = targetLevel;
        this.mustBeEqual = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int actualLevel = data.getCollectionSkills().getLevel(professionName);
        final int targetLevel = targetLevelVar.getValue(profile).intValue();

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
