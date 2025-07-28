package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Skript event, which listens to custom event fired by BetonQuest's event
 */
@SuppressWarnings("PMD.CommentRequired")
public class SkriptEventBQ extends SkriptEvent {

    @SuppressWarnings("NullAway.Init")
    private Literal<?> literal;

    /**
     * Constructs a new ScriptEvent for BetonQuest custom events.
     */
    public SkriptEventBQ() {
        super();
    }

    @Override
    public String toString(@Nullable final Event event, final boolean debug) {
        return "on betonquest event";
    }

    @Override
    public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parseResult) {
        literal = args[0];
        return true;
    }

    @Override
    public boolean check(final Event event) {
        return event instanceof final BQEventSkript.CustomEventForSkript scriptEvent && literal.check(event, (Checker<Object>) other ->
                other instanceof final String identifier && scriptEvent.getID().equals(identifier));
    }
}
