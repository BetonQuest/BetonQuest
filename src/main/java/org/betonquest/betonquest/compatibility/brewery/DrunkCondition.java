package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

@SuppressWarnings("PMD.CommentRequired")
public class DrunkCondition extends Condition {

    private final Integer drunkness;

    public DrunkCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        drunkness = instruction.getInt();

        if (drunkness < 0 || drunkness > 100) {
            throw new InstructionParseException("Drunkness can only be between 0 and 100!");
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final BPlayer bPlayer = BPlayer.get(profile.getOnlineProfile().get().getPlayer());
        return bPlayer != null && bPlayer.getDrunkeness() >= drunkness;
    }
}
