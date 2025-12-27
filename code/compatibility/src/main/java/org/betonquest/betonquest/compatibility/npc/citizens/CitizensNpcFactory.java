package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.bukkit.plugin.Plugin;

/**
 * Creates validated Npc Wrapper for Citizens Npcs.
 */
public class CitizensNpcFactory implements NpcFactory {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Create a new Npc Factory with a specific registry.
     *
     * @param plugin   the plugin instance
     * @param registry the registry of NPCs to use
     */
    public CitizensNpcFactory(final Plugin plugin, final NPCRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public NpcWrapper<NPC> parseInstruction(final Instruction instruction) throws QuestException {
        final boolean byName = instruction.bool().getFlag("byName", false)
                .getValue(null).orElse(false);
        if (byName) {
            return new CitizensNameWrapper(plugin, registry, instruction.string().get());
        }
        final Argument<Number> npcId = instruction.number().atLeast(0).get();
        return new CitizensWrapper(plugin, registry, npcId);
    }
}
