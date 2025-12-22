package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * How the MythicMob Npc is identified.
 */
public enum Type {
    /**
     * Identifies the Npc by its {@link MythicMob} (type).
     */
    MYTHIC_MOB {
        @Override
        protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MythicHider mythicHider,
                                              final MobExecutor mobExecutor) throws QuestException {
            final Variable<MythicMob> mythicMobVariable = instruction.get(string -> {
                final Optional<MythicMob> mythicMob = mobExecutor.getMythicMob(string);
                if (mythicMob.isPresent()) {
                    return mythicMob.get();
                }
                throw new QuestException("There exists no MythicMob type '" + string + "'");
            });
            return new TypeWrapper(mythicMobVariable, mythicHider, mobExecutor);
        }

        @Override
        protected String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getType().getInternalName();
        }
    },
    /**
     * Identifies the Npc by entity {@link java.util.UUID}.
     */
    UUID {
        @Override
        protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MythicHider mythicHider,
                                              final MobExecutor mobExecutor) throws QuestException {
            try {
                return new UUIDWrapper(instruction.get(instruction.getParsers().uuid()), mythicHider, mobExecutor);
            } catch (final IllegalArgumentException exception) {
                throw new QuestException(exception);
            }
        }

        @Override
        protected String toInstructionString(final ActiveMob mob) {
            return name() + " " + mob.getUniqueId().toString();
        }
    },
    /**
     * Identifies the Npc by its {@link ActiveMob#getFaction()}.
     */
    FACTION {
        @Override
        protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MythicHider mythicHider,
                                              final MobExecutor mobExecutor) throws QuestException {
            return new FactionWrapper(instruction.get(instruction.getParsers().string()), mythicHider, mobExecutor);
        }

        @Override
        protected String toInstructionString(final ActiveMob mob) {
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
    protected abstract NpcWrapper<ActiveMob> parse(Instruction instruction, MythicHider mythicHider,
                                                   MobExecutor mobExecutor) throws QuestException;

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
     * @param mythicHider the hider for mobs
     * @param mobExecutor the instance to get the mob from
     */
    private record UUIDWrapper(Variable<UUID> uuid, MythicHider mythicHider,
                               MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {

        @Override
        public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
            final UUID uuid = this.uuid.getValue(profile);
            final Optional<ActiveMob> activeMob = mobExecutor.getActiveMob(uuid);
            if (activeMob.isPresent()) {
                return new MythicMobsNpcAdapter(activeMob.get(), mythicHider);
            }
            throw new QuestException("Could not find entity '" + uuid + "' for MythicMob Npc");
        }
    }

    /**
     * Gets the Mob Npc by their {@link MythicMob} definition.
     *
     * @param type        the identifying type
     * @param mythicHider the hider for mobs
     * @param mobExecutor the instance to get the mob from
     */
    private record TypeWrapper(Variable<MythicMob> type, MythicHider mythicHider,
                               MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {

        @Override
        public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
            final MythicMob type = this.type.getValue(profile);
            final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> type.equals(mob.getType()));
            if (activeMobs.isEmpty()) {
                return new WrappingMMNpcAdapter(type, mythicHider);
            }
            final int one = 1;
            if (activeMobs.size() != one) {
                throw new QuestException("There exists multiple MythicMobs with type '" + type + "', can't determine!");
            }
            return new MythicMobsNpcAdapter(activeMobs.iterator().next(), mythicHider);
        }
    }

    /**
     * Gets the Mob Npc by their {@link ActiveMob#getFaction()}.
     *
     * @param faction     the identifying faction
     * @param mythicHider the hider for mobs
     * @param mobExecutor the instance to get the mob from
     */
    private record FactionWrapper(Variable<String> faction, MythicHider mythicHider,
                                  MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {

        @Override
        public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
            final String faction = this.faction.getValue(profile);
            final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> faction.equals(mob.getFaction()));
            if (activeMobs.isEmpty()) {
                throw new QuestException("There is no active mob with the faction '" + faction + "'!");
            }
            final int one = 1;
            if (activeMobs.size() != one) {
                throw new QuestException("There exists multiple MythicMobs with faction '" + faction + "', can't determine!");
            }
            return new MythicMobsNpcAdapter(activeMobs.iterator().next(), mythicHider);
        }
    }
}
