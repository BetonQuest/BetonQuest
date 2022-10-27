package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class DecentHologramsIntegrator extends HologramIntegrator {

    public DecentHologramsIntegrator() {
        super("DecentHolograms", DecentHologramsHologram.class, "2.7.3");
    }

    @Override
    public void hook(final String pluginName) throws HookException {
        super.hook(pluginName);
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            LOG.warn("Holograms from DecentHolograms will not be able to use BetonQuest variables in text-lines" +
                    "without PlaceholderAPI plugin! Install it to use holograms with variables!");
        }
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack:objective.kills.left% to
           %betonquest_pack:objective.kills.left% which is parsed by HolographicDisplays as a custom API placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            String variable = match.group();
            final String[] split = variable.split(":");
            final String packName = split.length > 1
                    ? split[0].substring(1)
                    : pack.getPackagePath();
            variable = split.length > 1
                    ? variable.substring(packName.length() + 2)
                    : variable;
            return "%betonquest_" + packName + ":" + variable.replaceAll("(%|\\$)", "") + "%";
        });
    }
}
