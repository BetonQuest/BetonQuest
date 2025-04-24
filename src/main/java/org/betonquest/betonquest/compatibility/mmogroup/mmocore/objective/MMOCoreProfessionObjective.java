package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * An objective that listens for the player leveling up in their MMOCore profession.
 */
public class MMOCoreProfessionObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The name of the profession that the player needs to level up.
     */
    @Nullable
    private final String professionName;

    /**
     * The target level to be reached.
     */
    private final Variable<Number> targetLevel;

    /**
     * Constructor for the MMOCoreProfessionObjective.
     *
     * @param instruction    the instruction object representing the objective
     * @param log            the logger for this objective
     * @param professionName the name of the profession to be leveled up
     * @param targetLevel    the target level to be reached
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOCoreProfessionObjective(final Instruction instruction, final BetonQuestLogger log, @Nullable final String professionName, final Variable<Number> targetLevel) throws QuestException {
        super(instruction);
        this.log = log;
        this.professionName = professionName;
        this.targetLevel = targetLevel;
    }

    /**
     * Listens for the player leveling up in their MMOCore profession.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(final PlayerLevelUpEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        final Profession profession = event.getProfession();
        if (profession != null && !profession.getName().equalsIgnoreCase(professionName)) {
            return;
        }
        try {
            if (event.getNewLevel() < targetLevel.getValue(onlineProfile).intValue()) {
                return;
            }
        } catch (final QuestException e) {
            log.warn("Error while getting target level for MMOCoreProfessionObjective: " + e.getMessage(), e);
        }
        completeObjective(onlineProfile);
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
