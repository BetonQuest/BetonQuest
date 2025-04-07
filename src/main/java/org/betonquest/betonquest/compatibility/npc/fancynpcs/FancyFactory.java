package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to get FancyNpcs Npcs.
 */
public class FancyFactory implements NpcFactory {
    /**
     * The empty default constructor.
     */
    public FancyFactory() {
    }

    @Override
    public NpcWrapper<Npc> parseInstruction(final Instruction instruction) throws QuestException {
        final NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        return new FancyWrapper(npcManager, instruction.next(), instruction.hasArgument("byName"));
    }
}
