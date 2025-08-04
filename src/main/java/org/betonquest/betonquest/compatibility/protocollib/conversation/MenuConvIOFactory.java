package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Menu conversation output.
 */
public class MenuConvIOFactory implements ConversationIOFactory {
    /**
     * Plugin instance to run tasks.
     */
    private final Plugin plugin;

    /**
     * the message parser to parse the configuration messages.
     */
    private final MessageParser messageParser;

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
     * @param plugin        the plugin instance to run tasks
     * @param messageParser the message parser to parse the configuration messages
     * @param fontRegistry  the font registry used for the conversation
     * @param config        the config accessor to the plugin's configuration
     * @param colors        the colors used for the conversation
     */
    public MenuConvIOFactory(final Plugin plugin, final MessageParser messageParser, final FontRegistry fontRegistry,
                             final ConfigAccessor config, final ConversationColors colors) {
        this.plugin = plugin;
        this.messageParser = messageParser;
        this.fontRegistry = fontRegistry;
        this.config = config;
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
        final MenuConvIOSettings settings = MenuConvIOSettings.fromConfigurationSection(messageParser, config.getConfigurationSection("conversation.io.menu"));
        final FixedComponentLineWrapper componentLineWrapper = new FixedComponentLineWrapper(fontRegistry, settings.lineLength());
        return new MenuConvIO(conversation, onlineProfile, colors, settings, componentLineWrapper, plugin, getControls(settings));
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private Map<MenuConvIO.CONTROL, MenuConvIO.ACTION> getControls(final MenuConvIOSettings settings) throws QuestException {
        final Map<MenuConvIO.CONTROL, MenuConvIO.ACTION> controls = new EnumMap<>(MenuConvIO.CONTROL.class);
        try {
            for (final MenuConvIO.CONTROL control : controls(settings.controlCancel())) {
                if (!controls.containsKey(control)) {
                    controls.put(control, MenuConvIO.ACTION.CANCEL);
                }
            }
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid data for 'control_cancel': " + settings.controlCancel(), e);
        }
        try {
            for (final MenuConvIO.CONTROL control : controls(settings.controlSelect())) {

                if (!controls.containsKey(control)) {
                    controls.put(control, MenuConvIO.ACTION.SELECT);
                }
            }
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid data for 'control_select': " + settings.controlSelect(), e);
        }
        try {
            for (final MenuConvIO.CONTROL control : controls(settings.controlMove())) {
                if (!controls.containsKey(control)) {
                    controls.put(control, MenuConvIO.ACTION.MOVE);
                }
            }
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid data for 'control_move': " + settings.controlMove(), e);
        }
        return controls;
    }

    private List<MenuConvIO.CONTROL> controls(final String string) {
        return Arrays.stream(string.split(","))
                .map(s -> s.toUpperCase(Locale.ROOT))
                .map(MenuConvIO.CONTROL::valueOf).toList();
    }
}
