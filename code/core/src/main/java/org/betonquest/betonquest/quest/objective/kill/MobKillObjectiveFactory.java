package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Factory for creating {@link MobKillObjective} instances from {@link Instruction}s.
 */
public class MobKillObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MobKillObjectiveFactory.
     */
    public MobKillObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<List<EntityType>> entities = instruction.enumeration(EntityType.class).list().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<String> name = instruction.string().get("name").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        final MobKillObjective objective = new MobKillObjective(instruction, targetAmount, entities, name, marked);
        service.request(MobKilledEvent.class).handler(objective::onMobKill, this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final MobKilledEvent event) {
        return event.getProfile().getOnlineProfile().map(OnlineProfile::getPlayer).orElse(null);
    }
}
