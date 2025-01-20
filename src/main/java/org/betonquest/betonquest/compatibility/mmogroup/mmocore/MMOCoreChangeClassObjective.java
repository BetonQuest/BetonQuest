package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
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
    private final String targetClassName;

    /**
     * <p>
     * Creates new instance of the objective.
     *
     * @param instruction Instruction object representing the objective;
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOCoreChangeClassObjective(final Instruction instruction) throws QuestException {
        super(instruction);

        targetClassName = instruction.getOptional("class");
    }

    /**
     * Listens for the player changing their MMOCore class.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onClassChange(final PlayerChangeClassEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }

        if (targetClassName == null) {
            completeObjective(onlineProfile);
            return;
        }

        if (targetClassName.equalsIgnoreCase(event.getNewClass().getName())) {
            completeObjective(onlineProfile);
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
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
