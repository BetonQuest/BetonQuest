package org.betonquest.betonquest.compatibility.denizen.action;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.core.TaskScriptContainer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Runs specified Denizen task script.
 */
public class DenizenTaskScriptAction implements PlayerAction {

    /**
     * The name of the script to run.
     */
    private final Argument<String> name;

    /**
     * Additional definitions for the Denizen script.
     */
    private final Map<String, Argument<String>> definitions;

    /**
     * Create a new Denizen Task Script Action.
     *
     * @param name        the name of the script to run
     * @param definitions additional definitions for the Denizen script
     */
    public DenizenTaskScriptAction(final Argument<String> name, final Map<String, Argument<String>> definitions) {
        this.name = name;
        this.definitions = definitions;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String name = this.name.getValue(profile);
        final TaskScriptContainer script = ScriptRegistry.getScriptContainerAs(name, TaskScriptContainer.class);
        if (script == null) {
            throw new QuestException("Could not find Denizen script: '%s'".formatted(name));
        }
        final BukkitScriptEntryData data = new BukkitScriptEntryData(PlayerTag.mirrorBukkitPlayer(profile.getPlayer()), null);
        final Map<String, String> resolvedDefinitions = new HashMap<>();
        for (final Map.Entry<String, Argument<String>> entry : definitions.entrySet()) {
            resolvedDefinitions.put(entry.getKey(), entry.getValue().getValue(profile));
        }
        script.run(data, null, scriptQueue -> resolvedDefinitions.forEach(scriptQueue::addDefinition));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
