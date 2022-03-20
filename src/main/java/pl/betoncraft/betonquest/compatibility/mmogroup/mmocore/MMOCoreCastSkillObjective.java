package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreCastSkillObjective extends Objective implements Listener {

    private final String skillId;

    public MMOCoreCastSkillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        template = ObjectiveData.class;
        skillId = instruction.next();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSkillCast(final PlayerCastSkillEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }
        final String skillName = event.getCast().getHandler().getId();
        if (!skillId.equalsIgnoreCase(skillName)) {
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
