package org.betonquest.betonquest.compatibility.mythicmobs.npc.type;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcAdapter;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract class to get a single or all available MythicMobs in a common way.
 */
public abstract class GenericMythicTypeWrapper implements NpcWrapper<ActiveMob> {

    /**
     * One amount.
     */
    private static final int ONE = 1;

    /**
     * MobExecutor instance to get active mobs from.
     */
    protected final MobExecutor mobExecutor;

    /**
     * Hider for mobs.
     */
    protected final MythicHider mythicHider;

    /**
     * Type string to use in exception messages.
     */
    protected final String type;

    /**
     * Creates a new instance.
     *
     * @param mobExecutor the instance to get the mob from
     * @param mythicHider the hider for mobs
     * @param type        the identifying string to use in exception messages
     */
    public GenericMythicTypeWrapper(final MobExecutor mobExecutor, final MythicHider mythicHider, final String type) {
        this.mobExecutor = mobExecutor;
        this.mythicHider = mythicHider;
        this.type = type;
    }

    /**
     * Gets a single matching mob.
     *
     * @param predicate    the predicate to select the mob
     * @param typeInstance the resolved identifier part to use in the exception message
     * @return the one mob matching the predicate
     * @throws QuestException when there is not exactly one mob
     */
    protected Npc<ActiveMob> getOne(final Predicate<ActiveMob> predicate, final String typeInstance) throws QuestException {
        final Set<Npc<ActiveMob>> all = getAll(predicate, typeInstance);
        if (all.size() != ONE) {
            throw new QuestException("There exists multiple MythicMobs with %s '%s', can't determine!".formatted(type, typeInstance));
        }
        return all.iterator().next();
    }

    /**
     * Gets all matching mobs.
     *
     * @param predicate    the predicate to select the mobs
     * @param typeInstance the resolved identifier part to use in the exception message
     * @return all mobs matching the predicate
     * @throws QuestException when there is no mob
     */
    protected Set<Npc<ActiveMob>> getAll(final Predicate<ActiveMob> predicate, final String typeInstance) throws QuestException {
        final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(predicate);
        if (activeMobs.isEmpty()) {
            throw new QuestException("There is no active mob with the %s '%s'!".formatted(type, typeInstance));
        }
        return activeMobs.stream()
                .map(activeMob -> new MythicMobsNpcAdapter(activeMob, mythicHider))
                .collect(Collectors.toSet());
    }
}
