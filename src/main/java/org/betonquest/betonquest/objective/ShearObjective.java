package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Requires the player to shear a sheep.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ShearObjective extends CountingObjective implements Listener {
    private static final Pattern UNDERSCORE = Pattern.compile("(?<!\\\\)_");

    private static final Pattern ESCAPED_UNDERSCORE = Pattern.compile("(\\\\)_");

    @Nullable
    private final DyeColor color;

    @Nullable
    private final String name;

    public ShearObjective(final Instruction instruction) throws QuestException {
        super(instruction, "sheep_to_shear");
        targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final String rawName = instruction.getOptional("name");
        name = rawName != null ? ESCAPED_UNDERSCORE.matcher(UNDERSCORE.matcher(rawName).replaceAll(" ")).replaceAll("_") : null;
        color = instruction.getEnum(instruction.getOptional("color"), DyeColor.class, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(final PlayerShearEntityEvent event) {
        if (event.getEntity().getType() != EntityType.SHEEP) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile)
                && (name == null || name.equals(event.getEntity().getCustomName()))
                && (color == null || color.equals(((Sheep) event.getEntity()).getColor()))
                && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
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
}
