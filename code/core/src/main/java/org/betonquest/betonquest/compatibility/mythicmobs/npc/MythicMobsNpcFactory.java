package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;

/**
 * Creates validated Npc Wrapper for MythicMobs Npcs.
 */
public class MythicMobsNpcFactory implements NpcFactory {
    /**
     * Instance to get mobs from the MythicMobs plugin.
     */
    private final MobExecutor mobExecutor;

    /**
     * Creates a new factory to get MythicMobs as Npcs from Instructions.
     *
     * @param mobExecutor to get mobs from the MythicMobs plugin
     */
    public MythicMobsNpcFactory(final MobExecutor mobExecutor) {
        this.mobExecutor = mobExecutor;
    }

    @Override
    public NpcWrapper<ActiveMob> parseInstruction(final Instruction instruction) throws QuestException {
        final Type type = instruction.get(Argument.ENUM(Type.class)).getValue(null);
        return type.parse(instruction, mobExecutor);
    }
}
