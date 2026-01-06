package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerTagAddEvent;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory to create {@link TagObjective}s from {@link Instruction}s.
 */
public class TagObjectiveFactory implements ObjectiveFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Tag Objective Factory.
     *
     * @param playerDataStorage the storage for player data
     */
    public TagObjectiveFactory(final PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final TagObjective objective = new TagObjective(service, playerDataStorage, instruction.packageIdentifier().get());
        service.request(PlayerTagAddEvent.class).handler(objective::onTag)
                .profile(PlayerTagAddEvent::getProfile).subscribe(false);
        service.request(PlayerObjectiveChangeEvent.class).handler(objective::onStart)
                .profile(PlayerObjectiveChangeEvent::getProfile).subscribe(false);
        return objective;
    }
}
