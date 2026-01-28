package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for creating {@link NPCKillObjective} instances from {@link Instruction}s.
 */
public class NPCKillObjectiveFactory implements ObjectiveFactory {

    /**
     * The beton quest api to use.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * The Citizens argument parser.
     */
    private final CitizensArgument citizensArgument;

    /**
     * Creates a new instance of the NPCKillObjectiveFactory.
     *
     * @param betonQuestApi the beton quest api to use
     * @param registry      the registry of NPCs to use
     * @throws QuestException if the identifier factory cannot be retrieved
     */
    public NPCKillObjectiveFactory(final BetonQuestApi betonQuestApi, final NPCRegistry registry) throws QuestException {
        this.betonQuestApi = betonQuestApi;
        this.registry = registry;
        this.citizensArgument = new CitizensArgument(betonQuestApi.getInstructionApi(),
                betonQuestApi.getQuestRegistries().identifiers().getFactory(NpcIdentifier.class));
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<NpcIdentifier> npcID = instruction.parse(citizensArgument).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        final NPCKillObjective objective = new NPCKillObjective(service, registry, targetAmount, npcID, betonQuestApi.getInstructionApi());
        service.request(MobKilledEvent.class).handler(objective::onNpcKill)
                .profile(MobKilledEvent::getProfile).subscribe(true);
        return objective;
    }
}
