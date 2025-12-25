package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOProfessionParser;

/**
 * Factory to create {@link MMOCoreProfessionExperienceEvent}s from {@link Instruction}s.
 */
public class MMOCoreProfessionExperienceEventFactory implements PlayerEventFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreProfessionExperienceEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Profession> profession = instruction.parse(MMOProfessionParser.PROFESSION).get();
        final Argument<Number> amount = instruction.number().get();
        final boolean isLevel = instruction.hasArgument("level");
        return new MMOCoreProfessionExperienceEvent(profession, amount, isLevel);
    }
}
