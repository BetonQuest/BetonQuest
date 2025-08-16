package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link QuestsEvent}s from {@link Instruction}s.
 */
public class QuestsEventFactory implements PlayerEventFactory {

    /**
     * Used Quests instance.
     */
    private final Quests quests;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the door event factory.
     *
     * @param quests active quests instance
     * @param data   the data for primary server thread access
     */
    public QuestsEventFactory(final Quests quests, final PrimaryServerThreadData data) {
        this.quests = quests;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> name = instruction.get(Argument.STRING);
        final boolean override = instruction.hasArgument("check-requirements");
        return new PrimaryServerThreadEvent(new QuestsEvent(quests, name, override), data);
    }
}
