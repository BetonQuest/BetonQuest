package pl.betoncraft.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
    private final List<TriggerType> triggerTypes = new ArrayList<>();

    /**
     * Parses the instruction and creates a new objective.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public MythicLibSkillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        template = ObjectiveData.class;
        skillId = instruction.next();
        final String triggerTypesString = instruction.getOptional("trigger");
        if (triggerTypesString == null) {
            triggerTypes.addAll(TriggerType.values());
        } else {
            triggerTypes.addAll(parseTriggerTypes(triggerTypesString));
        }
    }

    private Collection<TriggerType> parseTriggerTypes(final String triggerTypeString) throws InstructionParseException {
        final Collection<TriggerType> types = new ArrayList<>();
        final Collection<String> possibleTypes = new ArrayList<>();
        for (final TriggerType type : TriggerType.values()) {
            final String name = type.name();
            possibleTypes.add(name);
        }
        final String[] parts = triggerTypeString.toUpperCase(Locale.ROOT).split(",");
        for (final String part : parts) {
            if (!possibleTypes.contains(part)) {
                throw new InstructionParseException("Unknown trigger type: " + part);
            }
            final TriggerType triggerType = TriggerType.valueOf(part);
            types.add(triggerType);
        }
        return types;
    }

    /**
     * Whenever a player activates a skill, check if it is the skill we are looking for.
     *
     * @param event MythicLib skill cast event
     */
    @EventHandler(ignoreCancelled = true)
    public void onSkillCast(final SkillCastEvent event) {
        final String skillName = event.getCast().getHandler().getId();
        if (!skillId.equalsIgnoreCase(skillName) || !event.getResult().isSuccessful(event.getMetadata())) {
            return;
        }

        if (!triggerTypes.contains(event.getCast().getTrigger())) {
            return;
        }

        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
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
