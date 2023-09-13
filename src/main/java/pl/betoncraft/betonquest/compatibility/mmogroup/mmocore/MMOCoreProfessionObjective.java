package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreProfessionObjective extends Objective implements Listener {

    private final String professionName;
    private final int targetLevel;

    public MMOCoreProfessionObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        template = ObjectiveData.class;
        professionName = instruction.next();
        targetLevel = instruction.getInt();

    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(final PlayerLevelUpEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }
        final Profession profession = event.getProfession();
        if (profession == null) {
            return;
        }

        if (!profession.getName().equalsIgnoreCase(professionName) || event.getNewLevel() < targetLevel) {
            return;
        }
        completeObjective(playerID);
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
