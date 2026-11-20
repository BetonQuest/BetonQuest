package org.betonquest.betonquest.quest.condition.state;

import org.bukkit.entity.Player;

import java.util.function.Predicate;

/**
 * Enumeration of player states that can be checked in state conditions.
 * <p>
 * Each state represents a specific player condition (e.g., flying, sneaking, sprinting)
 * and provides a method to check if a player is currently in that state.
 */
public enum PlayerStates {

    /**
     * The player is blocking with a shield.
     */
    BLOCKING(Player::isBlocking),

    /**
     * The player can fly (e.g. in creative mode or by a plugin).
     */
    CAN_FLY(Player::getAllowFlight),

    /**
     * The player can pick up items.
     */
    CAN_PICKUP(Player::getCanPickupItems),

    /**
     * The player is currently climbing (e.g. on a ladder or vine).
     */
    CLIMBING(Player::isClimbing),

    /**
     * The player is currently being on fire.
     */
    FIRE(player -> player.getFireTicks() > 0),

    /**
     * The player is flying.
     */
    FLYING(Player::isFlying),

    /**
     * The player is frozen.
     */
    FROZEN(Player::isFrozen),

    /**
     * The player is gliding with an elytra.
     */
    GLIDING(Player::isGliding),

    /**
     * The player is currently glowing.
     */
    GLOWING(Player::isGlowing),

    /**
     * The player has their hand raised (e.g. eating, drinking, pulling a bow).
     */
    HAND_RAISED(Player::isHandRaised),

    /**
     * The player has a health scale applied.
     */
    HEALTH_SCALED(Player::isHealthScaled),

    /**
     * The player is inside a vehicle.
     */
    INSIDE_VEHICLE(Player::isInsideVehicle),

    /**
     * The player is invisible.
     */
    INVISIBLE(Player::isInvisible),

    /**
     * The player is invulnerable.
     */
    INVULNERABLE(Player::isInvulnerable),

    /**
     * The player is in a bubble column.
     */
    IN_BUBBLE_COLUMN(Player::isInBubbleColumn),

    /**
     * The player is in lava.
     */
    IN_LAVA(Player::isInLava),

    /**
     * The player is in powdered snow.
     */
    IN_POWDERED_SNOW(Player::isInPowderedSnow),

    /**
     * The player is in rain.
     */
    IN_RAIN(Player::isInRain),

    /**
     * The player is in water.
     */
    IN_WATER(Player::isInWater),

    /**
     * The player is currently jumping.
     */
    JUMPING(Player::isJumping),

    /**
     * The player is leashed.
     */
    LEASHED(Player::isLeashed),

    /**
     * The player is op (operator).
     */
    @SuppressWarnings("PMD.ShortVariable")
    OP(Player::isOp),

    /**
     * The player's time is relative to the server time.
     */
    PLAYER_TIME_RELATIVE(Player::isPlayerTimeRelative),

    /**
     * The player is currently using riptide.
     */
    RIPTIDING(Player::isRiptiding),

    /**
     * The player is sleeping.
     */
    SLEEPING(Player::isSleeping),

    /**
     * The player is being ignored by the sleep requirement.
     */
    SLEEPING_IGNORED(Player::isSleepingIgnored),

    /**
     * The player is sneaking.
     */
    SNEAKING(Player::isSneaking),

    /**
     * The player is sprinting.
     */
    SPRINTING(Player::isSprinting),

    /**
     * The player is swimming.
     */
    SWIMMING(Player::isSwimming);

    /**
     * Function to check the state of a player.
     */
    private final Predicate<Player> stateGetter;

    /**
     * Creates a new player state with the given state getter function.
     *
     * @param stateGetter function that checks if a player is in this state
     */
    PlayerStates(final Predicate<Player> stateGetter) {
        this.stateGetter = stateGetter;
    }

    /**
     * Checks if the given player is currently in this state.
     *
     * @param player the player to check
     * @return true if the player is in this state, false otherwise
     */
    public boolean checkState(final Player player) {
        return stateGetter.test(player);
    }
}
