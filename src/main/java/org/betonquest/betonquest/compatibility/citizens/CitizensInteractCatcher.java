package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.conversation.NpcInteractCatcher;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.objective.EntityInteractObjective;
import org.bukkit.event.EventHandler;

/**
 * Catches Citizens NPC interactions and adapts it into the BetonQuest event.
 */
public class CitizensInteractCatcher extends NpcInteractCatcher<NPC> {
    /**
     * Move Controller to check if the NPC blocks conversations while moving.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Initializes the catcher for Citizens.
     *
     * @param npcFactory             the factory to identify the clicked Npc
     * @param citizensMoveController the move controller to check if the NPC currently blocks conversations
     */
    public CitizensInteractCatcher(final NpcFactory<NPC> npcFactory, final CitizensMoveController citizensMoveController) {
        super(npcFactory);
        this.citizensMoveController = citizensMoveController;
    }

    private void interactLogic(final NPCClickEvent event, final EntityInteractObjective.Interaction interaction) {
        final NPC npc = event.getNPC();
        if (super.interactLogic(event.getClicker(), new CitizensAdapter(npc), interaction,
                citizensMoveController.blocksTalking(npc), event.isAsynchronous())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles right clicks.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCRightClickEvent event) {
        interactLogic(event, EntityInteractObjective.Interaction.RIGHT);
    }

    /**
     * Handles left click.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCLeftClickEvent event) {
        interactLogic(event, EntityInteractObjective.Interaction.LEFT);
    }
}
