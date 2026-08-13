package org.betonquest.betonquest.quest.action.hologram;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.compatibility.holograms.HologramIdentifier;
import org.betonquest.betonquest.compatibility.holograms.HologramLoop;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Action to update the visibility or content of holograms to one player.
 */
public class UpdateHologramAction implements OnlineAction {

    /**
     * Hologram loop with location holograms.
     */
    private final HologramLoop locationHologramLoop;

    /**
     * Hologram loop with npc holograms.
     */
    private final HologramLoop npcHologramLoop;

    /**
     * Action mode to execute with the holograms.
     */
    private final Argument<UpdateMode> mode;

    /**
     * Location holograms to update.
     */
    @Nullable
    private final Argument<List<HologramIdentifier>> hologramIdentifier;

    /**
     * Npc holograms to update.
     */
    @Nullable
    private final Argument<List<HologramIdentifier>> npcHologramIdentifier;

    /**
     * Create a new update action for holograms.
     * When no identifier is given at all, all holograms will be updated.
     *
     * @param locationHologramLoop  the hologram loop with location holograms
     * @param npcHologramLoop       the hologram loop with npc holograms
     * @param mode                  the action mode to execute with the holograms
     * @param hologramIdentifier    the location holograms to update
     * @param npcHologramIdentifier the npc holograms to update
     */
    public UpdateHologramAction(final HologramLoop locationHologramLoop, final HologramLoop npcHologramLoop, final Argument<UpdateMode> mode,
                                @Nullable final Argument<List<HologramIdentifier>> hologramIdentifier,
                                @Nullable final Argument<List<HologramIdentifier>> npcHologramIdentifier) {
        this.locationHologramLoop = locationHologramLoop;
        this.npcHologramLoop = npcHologramLoop;
        this.mode = mode;
        this.hologramIdentifier = hologramIdentifier;
        this.npcHologramIdentifier = npcHologramIdentifier;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final UpdateMode mode = this.mode.getValue(profile);
        if (hologramIdentifier == null && npcHologramIdentifier == null) {
            mode.accept(profile);
            return;
        }
        if (hologramIdentifier != null) {
            for (final HologramIdentifier identifier : hologramIdentifier.getValue(profile)) {
                mode.accept(profile, locationHologramLoop.get(identifier));
            }
        }
        if (npcHologramIdentifier != null) {
            for (final HologramIdentifier identifier : npcHologramIdentifier.getValue(profile)) {
                mode.accept(profile, npcHologramLoop.get(identifier));
            }
        }
    }
}
