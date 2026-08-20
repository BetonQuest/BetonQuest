package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.IntegrationData;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Display version and hook info.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class VersionSubCommand {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * The compatibility instance to use for compatibility checks.
     */
    private final Compatibility compatibility;

    /**
     * The betonquest updater.
     */
    private final Updater updater;

    /**
     * Create a new command for showing the version and hook info.
     *
     * @param plugin            the plugin to get the version from
     * @param constructorParams the constructor parameters
     */
    public VersionSubCommand(final Plugin plugin, final ConstructorParams constructorParams) {
        this.plugin = plugin;
        this.localizations = constructorParams.localizations();
        this.compatibility = constructorParams.compatibility();
        this.updater = constructorParams.updater();
    }

    /**
     * Displays the version and hook info.
     *
     * @param sender       the sender to show the info to
     * @param commandAlias the alias used to execute the command which should be used in the click command
     * @throws QuestException if a message could not be parsed
     */
    public void displayVersionInfo(final CommandSender sender, final String commandAlias) throws QuestException {
        final String updateCommand = "/" + commandAlias + " update";

        final Component hooked = displayVersionInfoHooked(compatibility.getBetonQuest());

        final TextComponent.Builder externalHooked = Component.text();
        for (final Map.Entry<String, List<IntegrationData>> entry : compatibility.getExternal().entrySet()) {
            final VariableComponent external = new VariableComponent(localizations.getMessage(null, "command_version_output.external_hook",
                    new VariableReplacement("plugin", Component.text(entry.getKey())),
                    new VariableReplacement("hooked", displayVersionInfoHooked(entry.getValue()))));
            externalHooked.append(external.resolve());
        }

        final Component update = displayVersionInfoUpdate(updater);
        final Component copy = displayVersionInfoCopy(sender);

        final VariableComponent baseContent = new VariableComponent(localizations.getMessage(null, "command_version_output.info",
                new VariableReplacement("version", Component.text(plugin.getDescription().getVersion())),
                new VariableReplacement("server", Component.text(Bukkit.getServer().getName() + " " + Bukkit.getServer().getVersion())),
                new VariableReplacement("hooked", hooked),
                new VariableReplacement("external_hooks", externalHooked.build())));
        final Component copyContent = baseContent.resolve(
                new VariableReplacement("update", Component.empty()),
                new VariableReplacement("copy", Component.empty()));
        final Component info = baseContent.resolve(
                new VariableReplacement("update", update.clickEvent(ClickEvent.suggestCommand(updateCommand))),
                new VariableReplacement("copy", copy.clickEvent(ClickEvent.copyToClipboard(PlainTextComponentSerializer.plainText().serialize(copyContent)))));
        sender.sendMessage(info);
    }

    private Component displayVersionInfoHooked(final List<IntegrationData> dataList) throws QuestException {
        final TextComponent.Builder hookedBuilder = Component.text();
        for (final IntegrationData data : dataList) {
            final List<Triple<String, String, String>> triples = data.getDisplayInfo();
            if (triples.isEmpty()) {
                continue;
            }
            if (!hookedBuilder.children().isEmpty()) {
                hookedBuilder.append(Component.text(", "));
            }
            final List<Component> components = new ArrayList<>();
            for (final Triple<String, String, String> triple : triples) {
                final Component message = localizations.getMessage(null, "command_version_output.hook",
                        new VariableReplacement("plugin", Component.text(triple.getLeft())),
                        new VariableReplacement("version", Component.text(triple.getMiddle())));
                components.add(message.hoverEvent(HoverEvent.showText(Component.text(triple.getRight()))));
            }
            if (components.size() == 1) {
                hookedBuilder.append(components.get(0));
            } else {
                final JoinConfiguration joinConfiguration = JoinConfiguration.builder()
                        .prefix(Component.text("["))
                        .separator(Component.text(", "))
                        .suffix(Component.text("]")).build();
                hookedBuilder.append(Component.join(joinConfiguration, components));
            }
        }
        return hookedBuilder.build();
    }

    private Component displayVersionInfoUpdate(final Updater updater) throws QuestException {
        if (!updater.isUpdateAvailable()) {
            return Component.empty();
        }
        return localizations.getMessage(null, "command_version_output.update",
                new VariableReplacement("version", Component.text(updater.getUpdateVersion())));
    }

    private Component displayVersionInfoCopy(final CommandSender sender) throws QuestException {
        if (sender instanceof ConsoleCommandSender) {
            return Component.empty();
        }
        return localizations.getMessage(null, "command_version_output.copy");
    }
}
