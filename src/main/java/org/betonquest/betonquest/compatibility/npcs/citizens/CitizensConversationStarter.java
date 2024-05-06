package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCConversationStarter;
import org.betonquest.betonquest.compatibility.npcs.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Starts new conversations with Citizen NPCs.
 */
public class CitizensConversationStarter extends NPCConversationStarter {
    /**
     * Move Controller to check if the NPC blocks conversations while moving.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Initializes the listener.
     *
     * @param loggerFactory          the logger factory to create logger for the started conversations
     * @param log                    the custom logger for this class
     * @param citizensMoveController the move controller to check if the NPC currently blocks conversations
     */
    public CitizensConversationStarter(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                                       final CitizensMoveController citizensMoveController) {
        super(loggerFactory, log);
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    protected Listener newLeftClickListener() {
        return new LeftClickListener();
    }

    @Override
    protected Listener newRightClickListener() {
        return new RightClickListener();
    }

    private boolean interactLogic(final NPCClickEvent event) {
        final NPC npc = event.getNPC();
        return !citizensMoveController.blocksTalking(npc) && super.interactLogic(event.getClicker(), () -> new CitizensBQAdapter(npc));
    }

    @Override
    protected void startConversation(final OnlineProfile onlineProfile, final ConversationID conversationID, final BQNPCAdapter npc) {
        if (!(npc instanceof CitizensBQAdapter)) {
            throw new IllegalArgumentException("The NPC Adapter is not a Citizens Adapter!");
        }
        new CitizensConversation(loggerFactory.create(CitizensConversation.class), onlineProfile, conversationID,
                npc.getLocation(), ((CitizensBQAdapter) npc).getCitizensNPC(), npc);
    }

    /**
     * A listener for right-clicking a Citizens NPC.
     */
    private class RightClickListener implements Listener {
        /**
         * Create a new RightClickListener for Citizens NPCs.
         */
        public RightClickListener() {
        }

        /**
         * Handles right clicks.
         *
         * @param event the event to handle
         */
        @EventHandler(ignoreCancelled = true)
        public void onNPCClick(final NPCRightClickEvent event) {
            if (interactLogic(event)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * A listener for left-clicking a Citizens NPC.
     */
    private class LeftClickListener implements Listener {
        /**
         * Create a new RightClickListener for Citizens NPCs.
         */
        public LeftClickListener() {
        }

        /**
         * Handles left click.
         *
         * @param event the event to handle
         */
        @EventHandler(ignoreCancelled = true)
        public void onNPCClick(final NPCLeftClickEvent event) {
            if (interactLogic(event)) {
                event.setCancelled(true);
            }
        }
    }
}
