package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * How the MythicMob NPC is identified.
 */
public enum Type {
    /**
     * Identifies the Npc by its {@link MythicMob} (type).
     */
    BY_MYTHIC_MOB {
        @Override
        protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MobExecutor mobExecutor) throws QuestException {
            final Optional<MythicMob> mythicMob = mobExecutor.getMythicMob(instruction.next());
            if (mythicMob.isPresent()) {
                return new TypeWrapper(mythicMob.get(), mobExecutor);
            }
            throw new QuestException("There exists no MythicMob type '" + instruction.current() + "'");
        }

        @Override
        protected String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getType().getInternalName();
        }
    },
    /**
     * Identifies the Npc by entity {@link UUID}.
     */
    BY_UUID {
        @Override
        protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MobExecutor mobExecutor) throws QuestException {
            try {
                return new UUIDWrapper(UUID.fromString(instruction.next()), mobExecutor);
            } catch (final IllegalArgumentException exception) {
                throw new QuestException(exception);
            }
        }

        @Override
        protected String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getUniqueId().toString();
        }
    };

    /**
     * Gets the Wrapper representing the instruction.
     *
     * @param instruction the instruction with already consumed type parameter
     * @param mobExecutor the instance to get MythicMobs from
     * @return a new validated wrapper
     * @throws QuestException if the instruction cannot be parsed or there is no valid target for it
     */
    protected abstract NpcWrapper<ActiveMob> parse(Instruction instruction, MobExecutor mobExecutor) throws QuestException;

    /**
     * Gets the instruction string which parsing would result in getting that mob.
     *
     * @param mob the mob to parse
     * @return the string that would get that mob as instruction
     */
    protected abstract String toInstructionString(ActiveMob mob);

    /**
     * Gets the Mob Npc by {@link UUID}.
     *
     * @param uuid        the identifying uuid
     * @param mobExecutor the instance to get the mob from
     */
    private record UUIDWrapper(UUID uuid, MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {
        @Override
        public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
            final Optional<ActiveMob> activeMob = mobExecutor.getActiveMob(uuid);
            if (activeMob.isPresent()) {
                return new MythicMobsNpcAdapter(activeMob.get());
            }
            throw new QuestException("Could not find entity '" + uuid + "' for MythicMob Npc");
        }
    }

    /**
     * Gets the Mob Npc by their {@link MythicMob} definition.
     *
     * @param type        the identifying type
     * @param mobExecutor the instance to get the mob from
     */
    private record TypeWrapper(MythicMob type, MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {
        @Override
        public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
            final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> mob.getType().equals(type));
            if (activeMobs.isEmpty()) {
                throw new QuestException("Could not find MythicMob for type '" + type + "'");
            }
            final int one = 1;
            if (activeMobs.size() != one) {
                // TODO mode for random choosing? in instruction: random, all nearest (-.,.-) or throw
                throw new QuestException("There exists multiple MythicMobs with type '" + type + "', can't determine!");
            }
            return new MythicMobsNpcAdapter(activeMobs.iterator().next());
        }
    }
}
