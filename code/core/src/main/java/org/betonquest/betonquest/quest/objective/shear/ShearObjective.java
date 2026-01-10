package org.betonquest.betonquest.quest.objective.shear;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Requires the player to shear a sheep.
 */
public class ShearObjective extends CountingObjective {

    /**
     * The color of the sheep to shear.
     */
    @Nullable
    private final Argument<DyeColor> color;

    /**
     * The name of the sheep to shear.
     */
    @Nullable
    private final Argument<String> name;

    /**
     * Constructor for the ShearObjective.
     *
     * @param service      the objective factory service
     * @param targetAmount the target amount of sheep to shear
     * @param name         the name of the sheep to shear
     * @param color        the color of the sheep to shear
     * @throws QuestException if there is an error in the instruction
     */
    public ShearObjective(final ObjectiveService service, final Argument<Number> targetAmount, @Nullable final Argument<String> name,
                          @Nullable final Argument<DyeColor> color) throws QuestException {
        super(service, targetAmount, "sheep_to_shear");
        this.name = name;
        this.color = color;
    }

    /**
     * Check if the player sheared the right sheep.
     *
     * @param event         the event that triggered when the player sheared the sheep
     * @param onlineProfile the profile of the player that sheared the sheep
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onShear(final PlayerShearEntityEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getEntity().getType() != EntityType.SHEEP) {
            return;
        }
        if ((name == null || name.getValue(onlineProfile).equals(event.getEntity().getCustomName()))
                && (color == null || color.getValue(onlineProfile) == ((Sheep) event.getEntity()).getColor())) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }
}
