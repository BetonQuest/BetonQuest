package org.betonquest.betonquest.compatibility.auraskills.action;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.util.Utils;

/**
 * Gives experience to a player in a skill.
 */
public class AuraSkillsExperienceAction implements PlayerAction {

    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * The amount of experience to give.
     */
    private final Argument<Number> amount;

    /**
     * The Skill name to give experience in.
     */
    private final Argument<String> name;

    /**
     * If the amount is a level and not experience.
     */
    private final FlagArgument<Boolean> level;

    /**
     * Create a new AuraSkills experience event.
     *
     * @param auraSkillsApi the {@link AuraSkillsApi}.
     * @param amount        the amount of experience to give the player in the skill.
     * @param name          the {@link Skill} name to give experience in.
     * @param level         {@code true} if the amount is a level. Otherwise {@code false} if the amount is experience.
     */
    public AuraSkillsExperienceAction(final AuraSkillsApi auraSkillsApi, final Argument<Number> amount,
                                      final Argument<String> name, final FlagArgument<Boolean> level) {
        this.auraSkillsApi = auraSkillsApi;
        this.amount = amount;
        this.name = name;
        this.level = level;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final SkillsUser user = auraSkillsApi.getUser(profile.getPlayerUUID());
        if (user == null) {
            return;
        }

        final NamespacedId namespacedId = NamespacedId.fromDefault(name.getValue(profile));
        final Skill skill = Utils.getNN(auraSkillsApi.getGlobalRegistry().getSkill(namespacedId), "Invalid skill name");
        final int amount = this.amount.getValue(profile).intValue();
        if (!level.getValue(profile).orElse(false)) {
            user.addSkillXpRaw(skill, amount);
            return;
        }

        final int currentLevel = user.getSkillLevel(skill);
        for (int i = 1; i <= amount; i++) {
            final double requiredXP = auraSkillsApi.getXpRequirements().getXpRequired(skill, currentLevel + i);
            user.addSkillXpRaw(skill, requiredXP);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
