package org.betonquest.betonquest.compatibility.thebrewingproject.action;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory for {@link DrunkenEventAction}.
 *
 * @param api the brewing project api
 */
public record DrunkenEventActionFactory(TheBrewingProjectApi api) implements PlayerActionFactory {

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> drunkenEventNameArgument = instruction.string().get();
        return new DrunkenEventAction(api, drunkenEventNameArgument);
    }
}
