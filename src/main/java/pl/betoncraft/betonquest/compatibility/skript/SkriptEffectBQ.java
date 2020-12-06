package pl.betoncraft.betonquest.compatibility.skript;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Skript effect, which fires specified BetonQuest's event
 */
@SuppressWarnings("PMD.CommentRequired")
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
    public String toString(final Event event, final boolean debug) {
        return "fire " + this.event.toString() + " for " + player.getSingle(event).getName();
    }

    @Override
    protected void execute(final Event event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String eventID = SkriptEffectBQ.this.event.getSingle(event);
                try {
                    BetonQuest.event(PlayerConverter.getID(player.getSingle(event)), new EventID(null, eventID));
                } catch (ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error when running Skript event - could not load '" + eventID + "' event: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }

            }
        }.runTask(BetonQuest.getInstance());
    }

}
