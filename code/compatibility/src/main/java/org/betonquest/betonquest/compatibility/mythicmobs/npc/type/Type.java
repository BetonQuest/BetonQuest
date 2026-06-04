package org.betonquest.betonquest.compatibility.mythicmobs.npc.type;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;

import java.util.Optional;

/**
 * How the MythicMob Npc is identified.
 */
public enum Type {
    /**
     * Identifies the Npc by its {@link MythicMob} (type).
     */
    MYTHIC_MOB {
        @Override
        public NpcWrapper<ActiveMob> parse(final Instruction instruction, final MythicHider mythicHider,
                                           final MobExecutor mobExecutor) throws QuestException {
            final Argument<MythicMob> mythicMob = instruction.parse(string -> {
                final Optional<MythicMob> mob = mobExecutor.getMythicMob(string);
                if (mob.isPresent()) {
                    return mob.get();
                }
                throw new QuestException("There exists no such MythicMob type: " + string);
            }).get();
            return new MMTypeWrapper(mobExecutor, mythicHider, mythicMob);
        }

        @Override
        public String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getType().getInternalName();
        }
    },
    /**
     * Identifies the Npc by entity {@link java.util.UUID}.
     */
    UUID {
        @Override
        public NpcWrapper<ActiveMob> parse(final Instruction instruction, final MythicHider mythicHider,
                                           final MobExecutor mobExecutor) throws QuestException {
            return new MMUUIDWrapper(mobExecutor, mythicHider, instruction.uuid().get());
        }

        @Override
        public String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getUniqueId().toString();
        }
    },
    /**
     * Identifies the Npc by its {@link ActiveMob#getFaction()}.
     */
    FACTION {
        @Override
        public NpcWrapper<ActiveMob> parse(final Instruction instruction, final MythicHider mythicHider,
                                           final MobExecutor mobExecutor) throws QuestException {
            return new MMFactionWrapper(mobExecutor, mythicHider, instruction.string().get());
        }

        @Override
        public String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getFaction();
        }
    };

    /**
     * Gets the Wrapper representing the instruction.
     *
     * @param instruction the instruction with already consumed type parameter
     * @param mythicHider the hider for mobs
     * @param mobExecutor the instance to get MythicMobs from
     * @return a new validated wrapper
     * @throws QuestException if the instruction cannot be parsed or there is no valid target for it
     */
    public abstract NpcWrapper<ActiveMob> parse(Instruction instruction, MythicHider mythicHider,
                                                MobExecutor mobExecutor) throws QuestException;

    /**
     * Gets the instruction string which parsing would result in getting that mob.
     *
     * @param mob the mob to parse
     * @return the string that would get that mob as instruction
     */
    public abstract String toInstructionString(ActiveMob mob);
}
