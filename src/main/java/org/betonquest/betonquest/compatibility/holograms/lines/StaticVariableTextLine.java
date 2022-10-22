package org.betonquest.betonquest.compatibility.holograms.lines;

import lombok.CustomLog;
import lombok.Getter;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Displays a simple text line with optional color codes and contains a global variable string
 */
@Getter
@CustomLog
public class StaticVariableTextLine extends AbstractLine {
    private final VariableString text;
    private final QuestPackage questPackage;

    /**
     * Creates a new instance of StaticVariableTextLine.
     *
     * @param questPackage the package of which this text resides
     * @param text         Text to be displayed
     * @throws InstructionParseException
     */
    public StaticVariableTextLine(final QuestPackage questPackage, final String text) throws InstructionParseException {
        super(false, 1);
        this.questPackage = questPackage;
        this.text = new VariableString(questPackage, text);
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) {
        hologram.setLine(index, text.getString(null));
    }
}
