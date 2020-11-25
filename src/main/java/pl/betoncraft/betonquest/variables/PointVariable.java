package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.util.List;

/**
 * Allows you to display total amount of points or amount of points remaining to
 * some other amount.
 */
public class PointVariable extends Variable {

    protected String category;
    protected Type type;
    protected int amount;

    public PointVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        category = instruction.next();
        if (category.contains("*")) {
            category = category.replace('*', '.');
        } else {
            category = instruction.getPackage().getName() + "." + category;
        }
        if ("amount".equalsIgnoreCase(instruction.next())) {
            type = Type.AMOUNT;
        } else if (instruction.current().toLowerCase().startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse point amount", e);
            }
        } else {
            throw new InstructionParseException(String.format("Unknown variable type: '%s'",
                    instruction.current()));
        }
    }

    @Override
    public String getValue(final String playerID) {
        return getValue(BetonQuest.getInstance().getPlayerData(playerID).getPoints());
    }

    protected String getValue(final List<Point> points) {
        Point point = null;
        for (final Point p : points) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                point = p;
                break;
            }
        }
        int count = 0;
        if (point != null) {
            count = point.getCount();
        }
        switch (type) {
            case AMOUNT:
                return Integer.toString(count);
            case LEFT:
                return Integer.toString(amount - count);
            default:
                return "";
        }
    }

    protected enum Type {
        AMOUNT, LEFT
    }

}
