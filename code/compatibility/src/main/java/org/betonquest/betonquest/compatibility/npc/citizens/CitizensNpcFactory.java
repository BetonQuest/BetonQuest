package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;

/**
 * Creates validated Npc Wrapper for Citizens Npcs.
 */
public class CitizensNpcFactory implements NpcFactory {

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Create a new Npc Factory with a specific registry.
     *
     * @param registry the registry of NPCs to use
     */
    public CitizensNpcFactory(final NPCRegistry registry) {
        this.registry = registry;
    }

    @Override
    public NpcWrapper<NPC> parseInstruction(final Instruction instruction) throws QuestException {
        final boolean byName = instruction.bool().getFlag("byName", true)
                .getValue(null).orElse(false);
        if (byName) {
            return new CitizensNameWrapper(registry, instruction.string().get());
        }
        final Argument<Number> npcId = instruction.number().atLeast(0).get();
        return new CitizensWrapper(registry, npcId);
    }
}
