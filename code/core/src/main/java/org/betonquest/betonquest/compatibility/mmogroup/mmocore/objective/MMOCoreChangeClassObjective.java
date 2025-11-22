package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * An objective that listens for the player changing their MMOCore class.
 */
public class MMOCoreChangeClassObjective extends Objective implements Listener {

    /**
     * The name of the class that the player needs to change to.
     */
    @Nullable
    private final Variable<String> targetClassName;

    /**
     * Constructor for the MMOCoreChangeClassObjective.
     *
     * @param instruction     Instruction object representing the objective;
     * @param targetClassName the name of the class to be changed to
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOCoreChangeClassObjective(final Instruction instruction, @Nullable final Variable<String> targetClassName) throws QuestException {
        super(instruction);
        this.targetClassName = targetClassName;
    }

    /**
     * Listens for the player changing their MMOCore class.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onClassChange(final PlayerChangeClassEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }

        if (targetClassName == null) {
            completeObjective(onlineProfile);
            return;
        }

        qeHandler.handle(() -> {
            if (targetClassName.getValue(onlineProfile).equalsIgnoreCase(event.getNewClass().getName())) {
                completeObjective(onlineProfile);
            }
        });
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
