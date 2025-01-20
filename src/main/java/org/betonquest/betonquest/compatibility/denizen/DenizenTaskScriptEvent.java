package org.betonquest.betonquest.compatibility.denizen;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.core.TaskScriptContainer;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Runs specified Denizen task script.
 */
@SuppressWarnings("PMD.CommentRequired")
public class DenizenTaskScriptEvent extends QuestEvent {

    private final String name;

    public DenizenTaskScriptEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        name = instruction.next();
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final TaskScriptContainer script = ScriptRegistry.getScriptContainerAs(name, TaskScriptContainer.class);
        if (script == null) {
            throw new QuestException("Could not find '" + name + "' Denizen script");
        }
        final BukkitScriptEntryData data = new BukkitScriptEntryData(PlayerTag.mirrorBukkitPlayer(profile.getPlayer()), null);
        script.run(data, null);
        return null;
    }
}
