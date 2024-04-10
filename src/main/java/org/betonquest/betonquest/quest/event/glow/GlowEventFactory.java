package org.betonquest.betonquest.quest.event.glow;

import fr.skytasul.glowingentities.GlowingBlocks;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.ChatColor;

/**
 * Eventfactory for the glow event.
 */
@SuppressWarnings("PMD.PreserveStackTrace")
public class GlowEventFactory implements EventFactory {
    /**
     * Glowing Blocks instance to allow for glowing blocks.
     */
    private final GlowingBlocks glowingBlocks;

    /**
     * Create the glow event factory.
     *
     * @param glowingBlocks GlowingBlocks instance to use.
     */
    public GlowEventFactory(final GlowingBlocks glowingBlocks) {
        this.glowingBlocks = glowingBlocks;
    }

    /**
     * @param instruction instruction to parse.
     * @return the parsed event.
     * @throws InstructionParseException if type,location or color can't be parsed.
     */
    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String typeString = instruction.next();
        final boolean type;
        switch (typeString) {
            case "add":
                type = true;
                break;
            case "delete":
                type = false;
                break;
            default:
                throw new InstructionParseException("Could not parse event type. Use 'add' or 'delete'");
        }
        final CompoundLocation location = instruction.getLocation(instruction.next());
        if (location == null) {
            throw new InstructionParseException("Error while parsing location");
        }
        final String colorOptional = instruction.getOptional("color");
        final ChatColor color = type && colorOptional != null ? getColor(colorOptional) : ChatColor.WHITE;
        final CompoundLocation region = instruction.getLocation(instruction.getOptional("region"));
        return new GlowEvent(location, region, glowingBlocks, color, type);
    }

    /**
     *
     * @param colorString String to turn into a ChatColor
     * @return ChatColor
     * @throws InstructionParseException if String can't be parsed into ChatColor
     */
    private ChatColor getColor(final String colorString) throws InstructionParseException {
        try {
            return ChatColor.valueOf(colorString);
        } catch (IllegalArgumentException ex) {
            throw new InstructionParseException("Error while parsing color '" + colorString + "'");
        }
    }
}
