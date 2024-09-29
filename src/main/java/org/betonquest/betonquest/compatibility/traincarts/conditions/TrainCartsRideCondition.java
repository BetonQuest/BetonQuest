package org.betonquest.betonquest.compatibility.traincarts.conditions;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * The TrainCarts ride condition checks if a player is currently riding a train from TrainCarts.
 */
public class TrainCartsRideCondition extends Condition {
    /**
     * The name of the train.
     */
    private final VariableString name;

    /**
     * Creates new instance of the condition. The condition should parse
     * instruction string at this point and extract all the data from it. If
     * anything goes wrong, throw {@link InstructionParseException} with an
     * error message describing the problem.
     *
     * @param instruction the Instruction object; you can get one from ID instance with
     *                    {@link ID#getInstruction()} or create it from an instruction
     *                    string
     * @throws InstructionParseException if the syntax is wrong or any error happens while parsing
     */
    public TrainCartsRideCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        persistent = false;
        staticness = false;

        final QuestPackage pack = instruction.getPackage();
        this.name = new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, instruction.getOptional("name", ""));
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final String nameFromInstruction = name.getValue(profile);
        if (nameFromInstruction.isEmpty()) {
            throw new QuestRuntimeException("The train name should not be empty!");
        }

        final Player player = profile.getPlayer().getPlayer();
        if (player == null) {
            return false;
        }
        final Entity vehicle = player.getVehicle();
        if (vehicle == null) {
            return false;
        }
        final MinecartMember<?> minecartMember = MinecartMemberStore.getFromEntity(vehicle);
        if (minecartMember == null) {
            return false;
        }
        final String trainName = minecartMember.getGroup().getProperties().getTrainName();
        return nameFromInstruction.equalsIgnoreCase(trainName);
    }
}
