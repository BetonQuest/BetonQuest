package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;

/**
 * Creates validated Npc Wrapper for MythicMobs Npcs.
 */
public class MythicMobsNpcFactory implements NpcFactory {
    /**
     * Instance to get mobs from the MythicMobs plugin.
     */
    private final MobExecutor mobExecutor;

    /**
     * Hider for Mobs.
     */
    private final MythicHider mythicHider;

    /**
     * Creates a new factory to get MythicMobs as Npcs from Instructions.
     *
     * @param mobExecutor to get mobs from the MythicMobs plugin
     * @param mythicHider the hider for mobs
     */
    public MythicMobsNpcFactory(final MobExecutor mobExecutor, final MythicHider mythicHider) {
        this.mobExecutor = mobExecutor;
        this.mythicHider = mythicHider;
    }

    @Override
    public NpcWrapper<ActiveMob> parseInstruction(final Instruction instruction) throws QuestException {
        final Type type = instruction.get(Argument.ENUM(Type.class)).getValue(null);
        return type.parse(instruction, mythicHider, mobExecutor);
    }
}
