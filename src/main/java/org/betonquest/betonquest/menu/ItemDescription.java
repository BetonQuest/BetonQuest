package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.menu.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains the description of a item in a menu. Variables are parsed and color codes are replaced.
 * <p>
 * Created on 27.01.2018
 *
 * @author Jonas Blocher
 */
public class ItemDescription {

    private final ConfigPackage pack;
    private final List<Line> lines;

    public ItemDescription(final ConfigPackage pack, final Collection<String> content) throws InstructionParseException {
        this.pack = pack;
        this.lines = new ArrayList<>();
        for (final String line : content) {
            new Line(line);
        }
    }

    /**
     * Receive display name of item for specific player
     *
     * @param playerID id of the player
     * @return
     */
    public String getDisplayName(final String playerID) {
        final Line displayName = this.lines.get(0);
        if (displayName == null) return null;
        return displayName.resolve(playerID);
    }

    /**
     * Receive lore of the item for specific player
     *
     * @param playerID id of the player
     * @return
     */
    public List<String> getLore(final String playerID) {
        final List<Line> lines = this.lines.subList(1, this.lines.size());
        if (lines.isEmpty()) return new ArrayList<>();
        final List<String> lore = new ArrayList<>(lines.size());
        for (final Line line : lines) {
            lore.add(line.resolve(playerID));
        }
        return lore;
    }

    /**
     * Helper class that simplifies parsing variables for a line
     */
    private class Line {

        private final String line;
        private final List<String> variables;

        public Line(final String line) throws InstructionParseException {
            //set line
            this.line = Utils.translateAlternateColorcodes('&', line);
            //find variables
            this.variables = new ArrayList<>();
            for (final String variable : BetonQuest.resolveVariables(line)) {
                try {
                    BetonQuest.createVariable(pack, variable);
                } catch (final InstructionParseException e) {
                    throw new InstructionParseException("Could not create '" + variable + "' variable: " + e.getMessage());
                }
                if (!variables.contains(variable))
                    variables.add(variable);
            }
            lines.add(this);
        }

        /**
         * Resolves all variables in this line for specified player
         *
         * @param playerID id of a player
         * @return
         */
        public String resolve(final String playerID) {
            String line = this.line;
            for (final String variable : variables) {
                line = line.replace(variable, BetonQuest.getInstance().getVariableValue(pack.getName(), variable, playerID));
            }
            return line;
        }
    }

}
