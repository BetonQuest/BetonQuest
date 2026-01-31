package org.betonquest.betonquest.quest.objective.password;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Factory for creating {@link PasswordObjective} instances from {@link Instruction}s.
 */
public class PasswordObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the PasswordObjectiveFactory.
     */
    public PasswordObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> patternString = instruction.string().get();
        final FlagArgument<Boolean> ignoreCase = instruction.bool().getFlag("ignoreCase", true);
        final Argument<Pattern> pattern = profile -> ignoreCase.getValue(profile).orElse(false)
                ? Pattern.compile(patternString.getValue(profile), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                : Pattern.compile(patternString.getValue(profile));
        final Argument<String> prefix = instruction.string().get("prefix").orElse(null);
        final String resolvedPrefix = prefix == null ? null : prefix.getValue(null);
        final String passwordPrefix = resolvedPrefix == null || resolvedPrefix.isEmpty() ? resolvedPrefix : resolvedPrefix + ": ";
        final Argument<List<ActionIdentifier>> failEvents = instruction.identifier(ActionIdentifier.class).list().get("fail", Collections.emptyList());
        final PasswordObjective objective = new PasswordObjective(service, pattern, passwordPrefix, failEvents);
        service.request(AsyncPlayerChatEvent.class).priority(EventPriority.LOW).onlineHandler(objective::onChat)
                .player(AsyncPlayerChatEvent::getPlayer).subscribe(true);
        service.request(PlayerCommandPreprocessEvent.class).priority(EventPriority.LOW).onlineHandler(objective::onCommand)
                .player(PlayerCommandPreprocessEvent::getPlayer).subscribe(true);
        return objective;
    }
}
