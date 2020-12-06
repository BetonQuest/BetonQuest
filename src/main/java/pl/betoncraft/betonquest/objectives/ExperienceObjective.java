package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player needs to get specified experience level
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceObjective extends Objective implements Listener {

    private final int amount;
    private final boolean checkForLevel;

    public ExperienceObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        this.amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        this.checkForLevel = instruction.hasArgument("level");
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChangeEvent(final PlayerLevelChangeEvent event) {
        if (!checkForLevel) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        onExperienceChange(playerID, event.getNewLevel());
    }

    @EventHandler(ignoreCancelled = true)
    public void onExpChangeEvent(final PlayerExpChangeEvent event) {
        if (checkForLevel) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        onExperienceChange(playerID, event.getPlayer().getTotalExperience() + event.getAmount());
    }

    private void onExperienceChange(final String playerID, final int newAmount) {
        if (!containsPlayer(playerID)) {
            return;
        }
        if (newAmount >= amount && checkConditions(playerID)) {
            completeObjective(playerID);
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
