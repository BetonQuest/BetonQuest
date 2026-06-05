package org.betonquest.betonquest.compatibility.denizen.action;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.core.TaskScriptContainer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Runs specified Denizen task script.
 */
public class DenizenTaskScriptAction implements PlayerAction {

    /**
     * The name of the script to run.
     */
    private final Argument<String> name;

    /**
     * The definition for the script to run with.
     */
    private final Argument<String> definition;

    /**
     * Create a new Denizen Task Script Action.
     *
     * @param name       the name of the script to run
     * @param definition the definition for the script to run with
     */
    public DenizenTaskScriptAction(final Argument<String> name, final Argument<String> definition) {
        this.name = name;
        this.definition = definition;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String name = this.name.getValue(profile);
        final TaskScriptContainer script = ScriptRegistry.getScriptContainerAs(name, TaskScriptContainer.class);
        if (script == null) {
            throw new QuestException("Could not find '" + name + "' Denizen script");
        }
        final BukkitScriptEntryData data = new BukkitScriptEntryData(PlayerTag.mirrorBukkitPlayer(profile.getPlayer()), null);
        final String definitionValue = definition.getValue(profile);
        script.run(data, null, scriptQueue -> scriptQueue.addDefinition("bq", definitionValue));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
