package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import lombok.CustomLog;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class HolographicDisplaysIntegrator extends HologramIntegrator {
    public HolographicDisplaysIntegrator() {
        super("HolographicDisplays", HolographicDisplaysHologram.class, "3.0.0-SNAPSHOT-b000", "SNAPSHOT-b");
    }

    @Override
    public void hook() throws HookException {
        super.hook();
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            LOG.warn("Holograms from HolographicDisplays won't be able to hide from players without ProtocolLib plugin! "
                    + "Install it to use conditioned holograms.");
        }
        final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(BetonQuest.getInstance());
        api.registerIndividualPlaceholder("bq", new HologramPlaceholder());
        api.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder());
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack:objective.kills.left% to
           {bq:pack:objective.kills.left} which is parsed by HolographicDisplays as a custom API placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            String variable = match.group();
            final String placeholder = variable.startsWith("$") && variable.endsWith("$") ? "{bqg:" : "{bq:";
            final String[] split = variable.split(":");
            final String packName = split.length > 1
                    ? split[0].substring(1)
                    : pack.getPackagePath();
            variable = split.length > 1
                    ? variable.substring(packName.length() + 2)
                    : variable;
            return placeholder + packName + ":" + variable.replaceAll("(%|\\$)", "") + "}";
        });
    }
}
