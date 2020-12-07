package pl.betoncraft.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.events.fake.FakeBrewEvent;
import org.bukkit.event.EventHandler;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Requires the player to manually brew a potion.
 */
@SuppressWarnings("PMD.CommentRequired")
public class BrewObjective extends pl.betoncraft.betonquest.objectives.BrewObjective {

    public BrewObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrew(final FakeBrewEvent event) {
        super.onBrew(event);
    }
}
