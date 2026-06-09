package org.betonquest.betonquest.compatibility.tbp.action;

import dev.jsinco.brewery.api.event.DrunkEvent;
import dev.jsinco.brewery.api.util.BreweryKey;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory fpr running drunken events.
 *
 * @param api The brewing project api.
 */
public record DrunkenEventActionFactory(TheBrewingProjectApi api) implements PlayerActionFactory {

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> drunkenEventNameArgument = instruction.string().get();
        return player -> {
            final String drunkenEventName = drunkenEventNameArgument.getValue(player);
            final DrunkEvent drunkEvent = api.getDrunkenEventManager().allEvents()
                    .stream()
                    .filter(event -> event.key().equals(BreweryKey.parse(drunkenEventName)))
                    .findFirst()
                    .orElseThrow(() -> new QuestException(String.format("Unknown drunken event: " + drunkenEventName)));
            api.getDrunkenEventManager().runEvent(player.getPlayerUUID(), drunkEvent);
        };
    }
}
