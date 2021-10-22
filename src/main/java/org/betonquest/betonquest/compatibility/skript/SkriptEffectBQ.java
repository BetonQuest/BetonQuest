package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Skript effect, which fires specified BetonQuest's event
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class SkriptEffectBQ extends Effect {

    private Expression<String> event;
    private Expression<Player> player;

    public SkriptEffectBQ() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
        this.event = (Expression<String>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    @SuppressFBWarnings({"NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    public String toString(final Event event, final boolean debug) {
        return "fire " + this.event.toString() + " for " + player.getSingle(event).getName();
    }

    @Override
    protected void execute(final Event event) {
        new BukkitRunnable() {
            @Override
            @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
            public void run() {
                final String eventID = SkriptEffectBQ.this.event.getSingle(event);
                try {
                    BetonQuest.event(PlayerConverter.getID(player.getSingle(event)), new EventID(null, eventID));
                } catch (final ObjectNotFoundException e) {
                    LOG.warning("Error when running Skript event - could not load '" + eventID + "' event: " + e.getMessage(), e);
                }

            }
        }.runTask(BetonQuest.getInstance());
    }

}
