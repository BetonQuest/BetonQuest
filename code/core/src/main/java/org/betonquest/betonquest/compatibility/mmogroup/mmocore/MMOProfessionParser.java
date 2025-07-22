package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.manager.profession.ProfessionManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;

/**
 * Parses a string to a profession.
 */
public class MMOProfessionParser implements Argument<Profession> {
    /**
     * The default instance of {@link MMOProfessionParser}.
     */
    public static final MMOProfessionParser PROFESSION = new MMOProfessionParser(MMOCore.plugin.professionManager);

    /**
     * Manager to get professions.
     */
    private final ProfessionManager professionManager;

    /**
     * Create a new Profession parser.
     *
     * @param professionManager the manager to get professions
     */
    public MMOProfessionParser(final ProfessionManager professionManager) {
        this.professionManager = professionManager;
    }

    @Override
    public Profession apply(final String professionName) throws QuestException {
        final Profession profession = professionManager.get(professionName);
        if (profession == null) {
            throw new QuestException("Profession could not be found: " + professionName);
        }
        return profession;
    }
}
