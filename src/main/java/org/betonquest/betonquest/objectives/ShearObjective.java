package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.regex.Pattern;

/**
 * Requires the player to shear a sheep.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class ShearObjective extends CountingObjective implements Listener {

    private final String color;
    private final Pattern underscore = Pattern.compile("(?<!\\\\)_");
    private final Pattern escapedUnderscore = Pattern.compile("(\\\\)_");
    private final String name;

    public ShearObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "sheep_to_shear");
        targetAmount = instruction.getPositive();
        final String rawName = instruction.getOptional("name");
        name = rawName != null ? escapedUnderscore.matcher(underscore.matcher(rawName).replaceAll(" ")).replaceAll("_") : null;
        color = instruction.getOptional("color");
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onShear(final PlayerShearEntityEvent event) {
        if (event.getEntity().getType() != EntityType.SHEEP) {
            return;
        }
        final Profile profile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(profile)) {
            return;
        }
        if (name != null && (event.getEntity().getCustomName() == null || !event.getEntity().getCustomName().equals(name))) {
            return;
        }
        if (color != null && !((Sheep) event.getEntity()).getColor().toString().equalsIgnoreCase(color)) {
            return;
        }
        if (checkConditions(profile)) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
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
