package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for creating {@link NPCKillObjective} instances from {@link Instruction}s.
 */
public class NPCKillObjectiveFactory implements ObjectiveFactory {

    /**
     * The instruction api to use.
     */
    private final InstructionApi instructionApi;

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
     * @param citizensArgument the Citizens argument parser to use
     * @param instructionApi   the instruction api to use
     * @param registry         the registry of NPCs to use
     */
    public NPCKillObjectiveFactory(final CitizensArgument citizensArgument, final InstructionApi instructionApi, final NPCRegistry registry) {
        this.instructionApi = instructionApi;
        this.registry = registry;
        this.citizensArgument = citizensArgument;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<NpcIdentifier> npcID = instruction.parse(citizensArgument).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        final NPCKillObjective objective = new NPCKillObjective(service, registry, targetAmount, npcID, instructionApi);
        service.request(MobKilledEvent.class).handler(objective::onNpcKill)
                .profile(MobKilledEvent::getProfile).subscribe(true);
        return objective;
    }
}
