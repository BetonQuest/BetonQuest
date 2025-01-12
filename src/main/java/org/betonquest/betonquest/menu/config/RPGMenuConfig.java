package org.betonquest.betonquest.menu.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The plugins config file.
 */
public class RPGMenuConfig extends SimpleYMLSection {
    /**
     * The messages section of the configuration.
     */
    private static final String MESSAGES = "messages";

    /**
     * Default value if menus close when an item was clicked.
     */
    public final boolean defaultCloseOnClick;

    /**
     * Hashmap containing all messages for each language.
     */
    private final Map<String, Map<String, String>> messages;

    /**
     * List containing all languages.
     */
    private final List<String> languages;

    /**
     * Creates a new config for thr RPGMenu.
     *
     * @param menuConfigAccessor the provider for the config
     * @throws InvalidConfigurationException if the backing configuration is empty
     */
    @SuppressWarnings("NullAway")
    public RPGMenuConfig(final ConfigAccessor menuConfigAccessor) throws InvalidConfigurationException {
        super(null, "menuConfig.yml", menuConfigAccessor.getConfig());
        //load languages
        if (!config.contains(MESSAGES) || !config.isConfigurationSection(MESSAGES)) {
            throw new Missing(MESSAGES);
        }
        this.messages = new HashMap<>();
        this.languages = new ArrayList<>();
        languages.addAll(config.getConfigurationSection(MESSAGES).getKeys(false));
        //load configuration settings
        this.defaultCloseOnClick = getBoolean("default_close");

        //load all messages
        this.loadMessage("command_no_permission");
        this.loadMessage("menu_do_not_open");
        this.loadMessage("command_usage");
        this.loadMessage("command_info_reload");
        this.loadMessage("command_info_list");
        this.loadMessage("command_info_open");
        this.loadMessage("command_invalid_menu");
        this.loadMessage("command_invalid_player");
        this.loadMessage("command_no_player");
        this.loadMessage("command_no_menu");
        this.loadMessage("command_open_successful");
        this.loadMessage("command_reload_successful");
        this.loadMessage("command_reload_failed");
        this.loadMessage("command_list");
        this.loadMessage("click_to_open");
    }

    /**
     * Get a message in a specific language by its key.
     *
     * @param lang    language of the message
     * @param key     key of the message
     * @param replace arguments in the message that should be replaced
     * @return the predefined message with all args replaced
     */
    public String getMessage(@Nullable final String lang, final String key, final String... replace) {
        String message = Optional.ofNullable(lang)
                .map(messages::get).map(translations -> translations.get(key))
                .or(() -> Optional.ofNullable(Config.getLanguage())
                        .map(messages::get).map(translations -> translations.get(key)))
                .or(() -> Optional.of("en")
                        .map(messages::get).map(translations -> translations.get(key)))
                .orElse("null");

        for (int i = 1; i <= replace.length; i++) {
            message = message.replace("{" + i + "}", replace[i - 1]);
        }
        return message;
    }

    /**
     * Get a translated message for a command sender.
     *
     * @param sender  who the message should be displayed to
     * @param key     key of the message
     * @param replace arguments in the message that should be replaced
     * @return the predefined message with all args replaced
     */
    public String getMessage(final CommandSender sender, final String key, final String... replace) {
        String lang = null;
        if (sender instanceof Player) {
            lang = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getLanguage();
        }
        return getMessage(lang, key, replace);
    }

    /**
     * Sends a predefined message to a command sender.
     *
     * @param sender  the recipient of the message
     * @param key     the key of the message
     * @param replace arguments in the message that should be replaced
     */
    public void sendMessage(final CommandSender sender, final String key, final String... replace) {
        sender.sendMessage(getMessage(sender, key, replace));
    }

    /**
     * Load a message from file into hash map.
     *
     * @param key key of the message
     * @throws Missing if message isn't found in default language
     */
    private void loadMessage(final String key) throws Missing {
        for (final String lang : this.languages) {
            try {
                Map<String, String> msgs = messages.get(lang);
                if (msgs == null) {
                    msgs = new HashMap<>();
                }
                msgs.put(key, ChatColor.translateAlternateColorCodes('&', getString("messages." + lang + "." + key)).replace("\\n", "\n"));
                this.messages.put(lang, msgs);
            } catch (final Missing e) {
                if (lang.equals(Config.getLanguage())) {
                    throw e;
                }
            }
        }
    }
}
