package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Player has to right click the NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCInteractObjective extends Objective implements Listener {
    private final int npcId;

    private final boolean cancel;

    private final InteractionType interactionType;

    public NPCInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("ID cannot be negative");
        }
        cancel = instruction.hasArgument("cancel");
        interactionType = instruction.getEnum("interaction", InteractionType.class, InteractionType.RIGHT_CLICK);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCRightClick(final NPCRightClickEvent event) {
        if (interactionType.isRight()) {
            onNPCClick(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCLeftClick(final NPCLeftClickEvent event) {
        if (interactionType.isLeft()) {
            onNPCClick(event);
        }
    }

    private void onNPCClick(final NPCClickEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getClicker());
        if (event.getNPC().getId() != npcId || !containsPlayer(onlineProfile)) {
            return;
        }
        if (checkConditions(onlineProfile)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(onlineProfile);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

    private enum InteractionType {
        LEFT_CLICK(true, false),
        RIGHT_CLICK(false, true),
        BOTH(true, true);

        final boolean left;

        final boolean right;

        InteractionType(final boolean left, final boolean right) {
            this.left = left;
            this.right = right;
        }

        public boolean isLeft() {
            return left;
        }

        public boolean isRight() {
            return right;
        }
    }

}
