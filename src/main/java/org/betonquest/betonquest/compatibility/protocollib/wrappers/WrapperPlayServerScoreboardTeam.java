package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;

@SuppressWarnings({"PMD.CommentRequired", "PMD.UnusedPrivateField"})
public class WrapperPlayServerScoreboardTeam extends PacketHandlerDecorator {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;

    public WrapperPlayServerScoreboardTeam() {
        this(new DefaultPacketHandler(TYPE));
    }

    protected WrapperPlayServerScoreboardTeam(final PacketHandler packetHandler) {
        super(packetHandler);
        if (getPacketHandler().getType() != TYPE) {
            throw new IllegalArgumentException(getPacketHandler().getType() + " is not a packet of type " + TYPE);
        }
    }


    /**
     * Retrieve Team Name.
     * <p>
     * Notes: a unique name for the team. (Shared with scoreboard).
     *
     * @return The current Team Name
     */
    public String getName() {
        return getHandle().getStrings().read(0);
    }

    /**
     * Set Team Name.
     *
     * @param value - new value.
     */
    public void setName(final String value) {
        getHandle().getStrings().write(0, value);
    }

    /**
     * Retrieve Team Display Name.
     * <p>
     * Notes: only if Mode = 0 or 2.
     *
     * @return The current Team Display Name
     */
    public WrappedChatComponent getDisplayName() {
        return getHandle().getChatComponents().read(0);
    }

    /**
     * Set Team Display Name.
     *
     * @param value - new value.
     */
    public void setDisplayName(final WrappedChatComponent value) {
        getHandle().getChatComponents().write(0, value);
    }

    /**
     * Retrieve Team Prefix.
     * <p>
     * Notes: only if Mode = 0 or 2. Displayed before the players' name that are
     * part of this team.
     *
     * @return The current Team Prefix
     */
    public WrappedChatComponent getPrefix() {
        return getHandle().getChatComponents().read(1);
    }

    /**
     * Set Team Prefix.
     *
     * @param value - new value.
     */
    public void setPrefix(final WrappedChatComponent value) {
        getHandle().getChatComponents().write(1, value);
    }

    /**
     * Retrieve Team Suffix.
     * <p>
     * Notes: only if Mode = 0 or 2. Displayed after the players' name that are
     * part of this team.
     *
     * @return The current Team Suffix
     */
    public WrappedChatComponent getSuffix() {
        return getHandle().getChatComponents().read(2);
    }

    /**
     * Set Team Suffix.
     *
     * @param value - new value.
     */
    public void setSuffix(final WrappedChatComponent value) {
        getHandle().getChatComponents().write(2, value);
    }

    /**
     * Retrieve Name Tag Visibility.
     * <p>
     * Notes: only if Mode = 0 or 2. always, hideForOtherTeams, hideForOwnTeam,
     * never.
     *
     * @return The current Name Tag Visibility
     */
    public String getNameTagVisibility() {
        return getHandle().getStrings().read(1);
    }

    /**
     * Set Name Tag Visibility.
     *
     * @param value - new value.
     */
    public void setNameTagVisibility(final String value) {
        getHandle().getOptionalStructures().read(0).map((structure) ->
                structure.getStrings().write(0, value));
    }

    /**
     * Retrieve Color.
     * <p>
     * Notes: only if Mode = 0 or 2. Same as Chat colors.
     *
     * @return The current Color
     */
    public ChatColor getColor() {
        return getHandle().getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).read(0);
    }

    /**
     * Set Color.
     *
     * @param value - new value.
     */
    public void setColor(final ChatColor value) {
        getHandle().getOptionalStructures().read(0).map((structure) ->
                structure.getEnumModifier(ChatColor.class,
                                MinecraftReflection.getMinecraftClass("EnumChatFormat"))
                        .write(0, value));
    }

    /**
     * Get the collision rule.
     * Notes: only if Mode = 0 or 2. always, pushOtherTeams, pushOwnTeam, never.
     *
     * @return The current collision rule
     */
    public String getCollisionRule() {
        return getHandle().getStrings().read(2);
    }

    /**
     * Sets the collision rule.
     *
     * @param value - new value.
     */
    public void setCollisionRule(final String value) {
        getHandle().getStrings().write(2, value);
    }

    /**
     * Retrieve Players.
     * <p>
     * Notes: only if Mode = 0 or 3 or 4. Players to be added/remove from the
     * team. Max 40 characters so may be uuid's later
     *
     * @return The current Players
     */
    @SuppressWarnings("unchecked")
    public List<String> getPlayers() {
        return (List<String>) getHandle().getSpecificModifier(Collection.class)
                .read(0);
    }

    /**
     * Set Players.
     *
     * @param value - new value.
     */
    public void setPlayers(final List<String> value) {
        getHandle().getSpecificModifier(Collection.class).write(0, value);
    }

    /**
     * Retrieve Mode.
     * <p>
     * Notes: if 0 then the team is created. If 1 then the team is removed. If 2
     * the team team information is updated. If 3 then new players are added to
     * the team. If 4 then players are removed from the team.
     *
     * @return The current Mode
     */
    public int getMode() {
        return getHandle().getIntegers().read(0);
    }

    /**
     * Set Mode.
     *
     * @param value - new value.
     */
    public void setMode(final int value) {
        getHandle().getIntegers().write(0, value);
    }

    /**
     * Retrieve pack option data. Pack data is calculated as follows:
     *
     * <pre>
     * <code>
     * int data = 0;
     * if (team.allowFriendlyFire()) {
     *     data |= 1;
     * }
     * if (team.canSeeFriendlyInvisibles()) {
     *     data |= 2;
     * }
     * </code>
     * </pre>
     *
     * @return The current pack option data
     */
    public int getPackOptionData() {
        return getHandle().getIntegers().read(1);
    }

    /**
     * Set pack option data.
     *
     * @param value - new value
     * @see #getPackOptionData()
     */
    public void setPackOptionData(final int value) {
        getHandle().getIntegers().write(1, value);
    }
}
