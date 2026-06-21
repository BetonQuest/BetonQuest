package org.betonquest.betonquest.compatibility.thebrewingproject.action;

import dev.jsinco.brewery.api.event.DrunkEvent;
import dev.jsinco.brewery.api.util.BreweryKey;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * A drunken action.
 *
 * @param drunkenEventNameArgument Drunk event name argument
 * @param api                      The brewing project api
 */
public record DrunkenEventAction(Argument<String> drunkenEventNameArgument,
                                 TheBrewingProjectApi api) implements PlayerAction {

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String drunkenEventName = drunkenEventNameArgument.getValue(profile);
        final BreweryKey eventKey = BreweryKey.parse(drunkenEventName);
        final DrunkEvent drunkEvent = api.getDrunkenEventManager().allEvents()
                .stream()
                .filter(event -> event.key().equals(eventKey))
                .findFirst()
                .orElseThrow(() -> new QuestException("Unknown drunken event: %s".formatted(drunkenEventName)));
        api.getDrunkenEventManager().runEvent(profile.getPlayerUUID(), drunkEvent);
    }
}
