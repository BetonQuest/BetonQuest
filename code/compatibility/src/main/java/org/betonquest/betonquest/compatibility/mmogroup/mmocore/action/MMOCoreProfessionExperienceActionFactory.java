package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOProfessionParser;

/**
 * Factory to create {@link MMOCoreProfessionExperienceAction}s from {@link Instruction}s.
 */
public class MMOCoreProfessionExperienceActionFactory implements PlayerActionFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreProfessionExperienceActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Profession> profession = instruction.parse(MMOProfessionParser.PROFESSION).get();
        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> level = instruction.bool().getFlag("level", true);
        return new MMOCoreProfessionExperienceAction(profession, amount, level);
    }
}
