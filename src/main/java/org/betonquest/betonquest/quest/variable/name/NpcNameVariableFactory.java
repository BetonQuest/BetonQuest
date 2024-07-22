package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;

/**
 * Factory to create {@link NpcNameVariable}s from {@link Instruction}s.
 */
public class NpcNameVariableFactory implements PlayerVariableFactory {
    /**
     * Class to get {@link org.betonquest.betonquest.database.PlayerData} from.
     */
    private final BetonQuest plugin;

    /**
     * Create a NpcName variable factory.
     *
     * @param plugin the class to get the {@link org.betonquest.betonquest.database.PlayerData} used in the variable
     */
    public NpcNameVariableFactory(final BetonQuest plugin) {
        this.plugin = plugin;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) {
        return new NpcNameVariable(plugin);
    }
}
