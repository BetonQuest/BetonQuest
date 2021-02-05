package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Skript condition, which checks specified BetonQuest's condition
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class SkriptConditionBQ extends Condition {

    private Expression<Player> player;
    private Expression<String> condition;

    public SkriptConditionBQ() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] arg0, final int arg1, final Kleenean arg2, final ParseResult arg3) {
        player = (Expression<Player>) arg0[0];
        condition = (Expression<String>) arg0[1];
        return true;
    }

    @Override
    @SuppressFBWarnings({"NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    public String toString(final Event event, final boolean debug) {
        return player.getSingle(event).getName() + " meets " + condition.toString();
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public boolean check(final Event event) {
        final String conditionID = condition.getSingle(event);
        try {
            return BetonQuest.condition(PlayerConverter.getID(player.getSingle(event)), new ConditionID(null, conditionID));
        } catch (final ObjectNotFoundException e) {
            LOG.warning(null, "Error while checking Skript condition - could not load condition with ID '" + conditionID + "': " + e.getMessage(), e);
            return false;
        }
    }

}
