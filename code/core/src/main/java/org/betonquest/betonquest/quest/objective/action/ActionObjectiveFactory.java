package org.betonquest.betonquest.quest.objective.action;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

/**
 * Factory class for creating {@link ActionObjective} instances from {@link Instruction}s.
 */
public class ActionObjectiveFactory implements ObjectiveFactory {

    /**
     * The "any" keyword.
     */
    private static final String ANY = "any";

    /**
     * Creates a new instance of the ActionObjectiveFactory.
     */
    public ActionObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Click> action = instruction.enumeration(Click.class).get();
        final Argument<Optional<BlockSelector>> selector = instruction.blockSelector()
                .prefilterOptional(ANY, null).get();
        final FlagArgument<Boolean> exactMatch = instruction.bool().getFlag("exactMatch", true);
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final Argument<Number> range = instruction.number().get("range", 0);
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<Optional<EquipmentSlot>> hand = instruction.enumeration(EquipmentSlot.class)
                .validate(slot -> slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND, "Invalid hand value: '%s'")
                .prefilterOptional(ANY, null)
                .get("hand").orElse(null);
        final EquipmentSlot slot = hand == null ? null : hand.getValue(null).orElse(null);
        final ActionObjective objective = new ActionObjective(service, action, selector, exactMatch, loc, range, cancel, slot);
        service.request(PlayerInteractEvent.class).priority(EventPriority.LOWEST).onlineHandler(objective::onInteract)
                .player(PlayerInteractEvent::getPlayer).subscribe(false);
        return objective;
    }
}
