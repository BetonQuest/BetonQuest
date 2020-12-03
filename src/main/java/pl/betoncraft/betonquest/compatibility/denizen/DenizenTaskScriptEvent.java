package pl.betoncraft.betonquest.compatibility.denizen;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.core.TaskScriptContainer;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Runs specified Denizen task script.
 */
@SuppressWarnings("PMD.CommentRequired")
public class DenizenTaskScriptEvent extends QuestEvent {

    private final String name;

    public DenizenTaskScriptEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        name = instruction.next();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final TaskScriptContainer script = ScriptRegistry.getScriptContainerAs(name, TaskScriptContainer.class);
        if (script == null) {
            throw new QuestRuntimeException("Could not find '" + name + "' Denizen script");
        }
        final Player player = PlayerConverter.getPlayer(playerID);
        final BukkitScriptEntryData data = new BukkitScriptEntryData(PlayerTag.mirrorBukkitPlayer(player), null);
        script.run(data, null);
        return null;
    }

}
