package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Number;

public class DataHandler {

    private short data = 0;
    private Number number = Number.WHATEVER;

    private DataHandler() {
    }

    public void set(String data) throws InstructionParseException {
        if (data.endsWith("-")) {
            number = Number.LESS;
            data = data.substring(0, data.length() - 1);
        } else if (data.endsWith("+")) {
            number = Number.MORE;
            data = data.substring(0, data.length() - 1);
        } else {
            number = Number.EQUAL;
        }
        try {
            this.data = Short.valueOf(data);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse item data value", e);
        }
//		if (this.data < 0) {
//			throw new InstructionParseException("Item data value cannot be negative");
//		}
    }

    public short get() {
        return data;
    }

    public boolean check(final int data) {
        switch (number) {
            case WHATEVER:
                return true;
            case EQUAL:
                return this.data == data;
            case MORE:
                return this.data <= data;
            case LESS:
                return this.data >= data;
            default:
                return false;
        }
    }

}
