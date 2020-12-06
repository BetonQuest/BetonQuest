package pl.betoncraft.betonquest.compatibility.skript;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Skript condition, which checks specified BetonQuest's condition
 */
@SuppressWarnings("PMD.CommentRequired")
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
    public String toString(final Event event, final boolean debug) {
        return player.getSingle(event).getName() + " meets " + condition.toString();
    }

    @Override
    public boolean check(final Event event) {
        final String conditionID = condition.getSingle(event);
        try {
            return BetonQuest.condition(PlayerConverter.getID(player.getSingle(event)), new ConditionID(null, conditionID));
        } catch (ObjectNotFoundException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while checking Skript condition - could not load condition with ID '" + conditionID + "': " + e.getMessage());
            LogUtils.logThrowable(e);
            return false;
        }
    }

}
