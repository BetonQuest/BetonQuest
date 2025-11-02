package org.betonquest.betonquest.compatibility.packetevents.conversation;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.compatibility.packetevents.conversation.input.ConversationAction;
import org.betonquest.betonquest.compatibility.packetevents.conversation.input.ConversationInput;
import org.betonquest.betonquest.compatibility.packetevents.passenger.FakeArmorStandPassengerController;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIOFactory {
    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * Plugin instance to run tasks.
     */
    private final Plugin plugin;

    /**
     * the text parser to parse the configuration text.
     */
    private final TextParser textParser;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * The config accessor to the plugin's configuration.
     */
    private final ConfigAccessor config;

    /**
     * Create a new Menu conversation IO factory.
     *
     * @param packetEventsAPI the PacketEvents API instance
     * @param plugin          the plugin instance to run tasks
     * @param textParser      the text parser to parse the configuration text
     * @param fontRegistry    the font registry used for the conversation
     * @param config          the config accessor to the plugin's configuration
     * @param colors          the colors used for the conversation
     */
    public MenuConvIOFactory(final PacketEventsAPI<?> packetEventsAPI, final Plugin plugin, final TextParser textParser, final FontRegistry fontRegistry,
                             final ConfigAccessor config, final ConversationColors colors) {
        this.packetEventsAPI = packetEventsAPI;
        this.plugin = plugin;
        this.textParser = textParser;
        this.fontRegistry = fontRegistry;
        this.config = config;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final MenuConvIOSettings settings = MenuConvIOSettings.fromConfigurationSection(textParser, config.getConfigurationSection("conversation.io.menu"));
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, settings.lineLength());
        final BiFunction<Player, ConversationAction, ConversationInput> inputFunction = (player, control) ->
                new FakeArmorStandPassengerController(packetEventsAPI, player, control);
        return new MenuConvIO(inputFunction, conversation, onlineProfile, colors, settings, componentLineWrapper, plugin, getControls(settings));
    }

    private Map<MenuConvIO.CONTROL, MenuConvIO.ACTION> getControls(final MenuConvIOSettings settings) throws QuestException {
        final Map<MenuConvIO.CONTROL, MenuConvIO.ACTION> controls = new EnumMap<>(MenuConvIO.CONTROL.class);
        for (final MenuConvIO.CONTROL control : controls(settings.controlCancel(), "control_cancel")) {
            if (!controls.containsKey(control)) {
                controls.put(control, MenuConvIO.ACTION.CANCEL);
            }
        }
        for (final MenuConvIO.CONTROL control : controls(settings.controlSelect(), "control_select")) {
            if (!controls.containsKey(control)) {
                controls.put(control, MenuConvIO.ACTION.SELECT);
            }
        }
        for (final MenuConvIO.CONTROL control : controls(settings.controlMove(), "control_move")) {
            if (!controls.containsKey(control)) {
                controls.put(control, MenuConvIO.ACTION.MOVE);
            }
        }
        return controls;
    }

    private List<MenuConvIO.CONTROL> controls(final String string, final String name) throws QuestException {
        try {
            return Arrays.stream(string.split(","))
                    .map(s -> s.toUpperCase(Locale.ROOT))
                    .map(MenuConvIO.CONTROL::valueOf).toList();
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid data for '" + name + "': " + string, e);
        }
    }
}
