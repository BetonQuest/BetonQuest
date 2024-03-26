package org.betonquest.betonquest.api;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This kind of event will be called when player is joining the server
 * with the method {@link #onPlayerOnline(PlayerJoinEvent)}
 */
public abstract class OnlineQuestEvent extends QuestEvent {

    /**
     * Creates a new instance of the event. This constructor must parse the user-provided {@link Instruction}
     * by extracting relevant user input as object variables. These will be used later on
     * when the event is called in {@link #execute(Profile)}.
     * If anything goes wrong, throw an {@link InstructionParseException} with an error message that helps the user fix
     * the event.
     *
     * @param instruction the {@link Instruction} object for this event; you need to
     *                    extract all required data from it and throw an
     *                    {@link InstructionParseException} if there is anything wrong
     * @param forceSync   If set to true this executes the event on the servers main thread.
     *                    Otherwise, it will keep running on the current thread (which could also be the main thread!).
     * @throws InstructionParseException when there is an error during syntax or argument parsing
     */
    public OnlineQuestEvent(final Instruction instruction, final boolean forceSync) throws InstructionParseException {
        super(instruction, forceSync);
    }

    /**
     * Triggered when player joins the server
     *
     * @param event the player join event
     */
    public abstract void onPlayerOnline(@NotNull PlayerJoinEvent event);
}
