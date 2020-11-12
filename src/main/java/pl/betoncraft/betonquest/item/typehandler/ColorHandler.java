package pl.betoncraft.betonquest.item.typehandler;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.utils.Utils;

public class ColorHandler {

    private Color color = Bukkit.getServer().getItemFactory().getDefaultLeatherColor();
    private Existence colorE = Existence.WHATEVER;

    public ColorHandler() {
    }

    public void set(final String string) throws InstructionParseException {
        if (string.equalsIgnoreCase("none")) {
            colorE = Existence.FORBIDDEN;
            return;
        }
        color = Utils.getColor(string);
        colorE = Existence.REQUIRED;
    }

    public Color get() {
        return color;
    }

    public boolean check(final Color color) {
        switch (colorE) {
            case WHATEVER:
                return true;
            case REQUIRED:
            case FORBIDDEN: // if it's forbidden, this.color is default leather color (undyed)
                return color.equals(this.color);
            default:
                return false;
        }
    }
}
