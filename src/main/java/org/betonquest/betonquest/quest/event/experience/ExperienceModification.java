package org.betonquest.betonquest.quest.event.experience;

import com.eteirnum.core.EteirnumCore;
import com.eteirnum.core.player.attributes.PlayerAttributes;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents the type of modification with the given amount on the player's experience.
 */
public enum ExperienceModification {
    /**
     * Adds the given amount to the player's experience, just experience points.
     */
    ADD_EXPERIENCE("addExperience", (player, amount) -> {
        PlayerAttributes attribute = EteirnumCore.instance.getPlayerAttributesManager().get(player.getUniqueId());
        attribute.addExp((int) amount);
    }),
    /**
     * Adds the given amount to the player's experience, levels and or percentage to the next level.
     */
    ADD_LEVEL("addLevel", (player, amount) -> {
        PlayerAttributes attribute = EteirnumCore.instance.getPlayerAttributesManager().get(player.getUniqueId());
        attribute.addLevel((int) amount);
    }),
    /**
     * Sets the player's experience to the next level to the given amount.
     */
    SET_EXPERIENCE_BAR("setExperience", (player, amount) -> {
        PlayerAttributes attribute = EteirnumCore.instance.getPlayerAttributesManager().get(player.getUniqueId());
        attribute.setExp((int) amount);
    }),
    /**
     * Sets the player's experience-level to the given amount.
     */
    SET_LEVEL("setLevel", (player, amount) -> {
        PlayerAttributes attribute = EteirnumCore.instance.getPlayerAttributesManager().get(player.getUniqueId());
        attribute.setLevel((int) amount);
    });

    /**
     * The name of the ExperienceModification in the user-facing instruction.
     */
    private final String instructionName;

    /**
     * Instance of the calculator to calculate the experience.
     */
    private final Calculator calculator;

    ExperienceModification(final String instructionName, final Calculator calculator) {
        this.instructionName = instructionName;
        this.calculator = calculator;
    }

    /**
     * Gets the instance that matches the name.
     *
     * @param name the name used in the instruction
     * @return an {@link Optional}
     */
    public static Optional<ExperienceModification> getFromInstruction(final String name) {
        return Stream.of(values()).filter((modification) -> modification.instructionName.equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Calculates the new experience with the modification type.
     *
     * @param player the player
     * @param amount the amount
     */
    public void apply(final Player player, final float amount) {
        calculator.calculate(player, amount);
    }

    /**
     * Functional interface to calculate the experience.
     */
    private interface Calculator {
        /**
         * Calculates the amount to apply to the player.
         *
         * @param player the player to apply the amount to
         * @param amount the amount to apply
         * @return the calculated amount
         */
        void calculate(Player player, float amount);
    }
}
