package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;

@SuppressWarnings("PMD.CommentRequired")
public class DrunkCondition extends Condition {

    private final int drunkenness;

    public DrunkCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        drunkenness = instruction.getInt();

        if (drunkenness < 0 || drunkenness > 100) {
            throw new QuestException("Drunkenness can only be between 0 and 100!");
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final BPlayer bPlayer = BPlayer.get(profile.getOnlineProfile().get().getPlayer());
        return bPlayer != null && bPlayer.getDrunkeness() >= drunkenness;
    }
}
