package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOProfessionParser;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MMOCoreProfessionExperienceEvent}s from {@link Instruction}s.
 */
public class MMOCoreProfessionExperienceEventFactory implements PlayerEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Event Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreProfessionExperienceEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Profession> profession = instruction.get(MMOProfessionParser.PROFESSION);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final boolean isLevel = instruction.hasArgument("level");
        return new PrimaryServerThreadEvent(new MMOCoreProfessionExperienceEvent(profession, amount, isLevel), data);
    }
}
