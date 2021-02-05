package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import org.bukkit.event.Event;

/**
 * Skript event, which listens to custom event fired by BetonQuest's event
 */
@SuppressWarnings("PMD.CommentRequired")
public class SkriptEventBQ extends SkriptEvent {

    private Literal<?> literal;

    public SkriptEventBQ() {
        super();
    }

    @Override
    public String toString(final Event event, final boolean debug) {
        return "on betonquest event";
    }

    @Override
    public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parseResult) {
        literal = args[0];
        return true;
    }

    @Override
    public boolean check(final Event event) {
        if (event instanceof BQEventSkript.CustomEventForSkript) {
            final BQEventSkript.CustomEventForSkript scriptEvent = (BQEventSkript.CustomEventForSkript) event;
            return literal.check(event, new Checker<Object>() {
                @Override
                public boolean check(final Object other) {
                    if (other instanceof String) {
                        final String identifier = (String) other;
                        return scriptEvent.getID().equals(identifier);
                    }
                    return false;
                }

            });
        }
        return false;
    }

}
