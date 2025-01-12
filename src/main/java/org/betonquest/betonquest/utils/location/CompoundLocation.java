package org.betonquest.betonquest.utils.location;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * This class parses various location related strings with or without {@link Variable}s.
 *
 * @deprecated Use {@link VariableLocation} instead
 */
@Deprecated
public class CompoundLocation extends VariableLocation {

    /**
     * This class parses a string into a {@link Location} and a {@link Vector}. The input string has
     * to be in the format 'x;y;z;world[;yaw;pitch][-&gt; (x;y;z)]'. All elements in square brackets are optional.
     * The last optional part is the {@link Vector} that will be added to the {@link Location} if specified.
     * Each part of the input string can be a {@link Variable} instead of an {@link Integer} or {@link String}.
     *
     * @param pack Name of the {@link QuestPackage} - required for {@link Variable} resolution
     * @param data string containing raw location in the defined format
     * @throws QuestException Is thrown when an error appears while parsing
     * @deprecated Use {@link VariableLocation#VariableLocation(VariableProcessor, QuestPackage, String)}
     */
    @Deprecated
    public CompoundLocation(final QuestPackage pack, final String data) throws QuestException {
        super(BetonQuest.getInstance().getVariableProcessor(), pack, data);
    }

    /**
     * This class parses a string into a {@link Location} and a {@link Vector}. The input string has
     * to be in the format 'x;y;z;world[;yaw;pitch][-&gt; (x;y;z)]'. All elements in square brackets are optional.
     * The last optional part is the {@link Vector} that will be added to the {@link Location} if specified.
     * Each part of the input string can be a {@link Variable} instead of an {@link Integer} or {@link String}.
     *
     * @param packName Name of the {@link QuestPackage} - required for {@link Variable} resolution
     * @param data     string containing raw location in the defined format
     * @throws QuestException Is thrown when an error appears while
     * @deprecated Use {@link VariableLocation#VariableLocation(VariableProcessor, QuestPackage, String)} instead
     */
    @Deprecated
    public CompoundLocation(final String packName, final String data) throws QuestException {
        this(getPack(packName), data);
    }

    private static QuestPackage getPack(final String packName) throws QuestException {
        final QuestPackage pack = Config.getPackages().get(packName);
        if (pack == null) {
            throw new QuestException("Package '" + packName + "' not found");
        }
        return pack;
    }

}
