package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * An objective that is completed when a player activates a MythicLib skill.
 */
public class MythicLibSkillObjective extends Objective implements Listener {

    /**
     * The name of the skill to activate.
     */
    private final String skillId;

    /**
     * Whether the skill must be "cast" by the player.
     * This indicates that the skill was triggered by MMOCore's ability system.
     */
    private final List<TriggerType> triggerTypes;

    /**
     * Parses the instruction and creates a new objective.
     *
     * @param instruction  the user-provided instruction
     * @param skillId      the skill ID to activate
     * @param triggerTypes the trigger types that will activate the skill
     * @throws QuestException if the instruction is invalid
     */
    public MythicLibSkillObjective(final Instruction instruction, final String skillId, final List<TriggerType> triggerTypes) throws QuestException {
        super(instruction);
        this.skillId = skillId;
        this.triggerTypes = triggerTypes;
    }

    /**
     * Whenever a player activates a skill, check if it is the skill we are looking for.
     *
     * @param event MythicLib skill cast event
     */
    @EventHandler(ignoreCancelled = true)
    public void onSkillCast(final SkillCastEvent event) {
        final String skillName = event.getCast().getHandler().getId();
        if (!skillId.equalsIgnoreCase(skillName) || !event.getResult().isSuccessful()) {
            return;
        }

        if (!triggerTypes.contains(event.getCast().getTrigger())) {
            return;
        }

        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
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
