package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Creates validated Npc Wrapper for Citizens Npcs.
 */
public class CitizensNpcFactory implements NpcFactory {
    /**
     * The default Constructor.
     */
    public CitizensNpcFactory() {
    }

    @Override
    public NpcWrapper<NPC> parseInstruction(final Instruction instruction) throws QuestException {
        if (instruction.hasArgument("byName")) {
            return new CitizensNameWrapper(instruction.next());
        }
        final Variable<Number> npcId = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ZERO);
        return new CitizensWrapper(npcId);
    }
}
