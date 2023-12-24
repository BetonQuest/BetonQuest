package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Contains the description of a item in a menu. Variables are parsed and color codes are replaced.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ItemDescription {
    private final QuestPackage pack;

    private final List<Line> lines;

    public ItemDescription(final QuestPackage pack, final Collection<String> content) throws InstructionParseException {
        this.pack = pack;
        this.lines = new ArrayList<>();
        for (final String line : content) {
            new Line(line);
        }
    }

    /**
     * Receive display name of item for specific player
     *
     * @param profile the {@link Profile} of the player
     * @return the item's display name for the specified player.
     */
    public String getDisplayName(final Profile profile) {
        final Line displayName = this.lines.get(0);
        if (displayName == null) {
            return null;
        }
        return displayName.resolve(profile);
    }

    /**
     * Receive lore of the item for specific player
     *
     * @param profile the {@link Profile} of the player
     * @return the item's lore for the specified player.
     */
    public List<String> getLore(final Profile profile) {
        final List<Line> lines = this.lines.subList(1, this.lines.size());
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }
        final List<String> lore = new ArrayList<>(lines.size());
        for (final Line line : lines) {
            lore.addAll(Arrays.asList(line.resolve(profile).split("\n")));
        }
        return lore;
    }

    /**
     * Helper class that simplifies parsing variables for a line
     */
    @SuppressWarnings("PMD.ShortClassName")
    private class Line {
        @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
        private final String line;

        private final List<String> variables;

        public Line(final String line) throws InstructionParseException {
            //set line
            this.line = line;
            //find variables
            this.variables = new ArrayList<>();
            for (final String variable : BetonQuest.resolveVariables(line)) {
                try {
                    BetonQuest.createVariable(pack, variable);
                } catch (final InstructionParseException e) {
                    throw new InstructionParseException("Could not create '" + variable + "' variable: " + e.getMessage(), e);
                }
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
            }
            lines.add(this);
        }

        /**
         * Resolves all variables in this line for specified player
         *
         * @param profile the {@link Profile} of the player
         * @return
         */
        public String resolve(final Profile profile) {
            String line = this.line;
            for (final String variable : variables) {
                line = line.replace(variable, BetonQuest.getInstance().getVariableValue(pack.getQuestPath(), variable, profile));
            }
            return ChatColor.translateAlternateColorCodes('&', line);
        }
    }

}
