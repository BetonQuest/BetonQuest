package org.betonquest.betonquest.quest.objective.brew;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Factory for creating {@link BrewObjective} instances from {@link Instruction}s.
 */
public class BrewObjectiveFactory implements ObjectiveFactory {

    /**
     * Profile provider to get the profile of the player.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new instance of the BrewObjectiveFactory.
     *
     * @param profileProvider the profile provider to get the profile of the player
     */
    public BrewObjectiveFactory(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<ItemWrapper> potion = instruction.item().get();
        final Argument<Number> targetAmount = instruction.number().atLeast(0).get();
        final BrewObjective objective = new BrewObjective(instruction, targetAmount, profileProvider, potion);
        service.request(InventoryClickEvent.class).priority(EventPriority.LOWEST)
                .handler(objective::onIngredientPut, InventoryClickEvent::getWhoClicked).subscribe(false);
        service.request(BrewEvent.class).priority(EventPriority.MONITOR)
                .handler(objective::onBrew).subscribe(true);
        return objective;
    }
}
