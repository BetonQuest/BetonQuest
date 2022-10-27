package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import lombok.CustomLog;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramSubIntegrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class HolographicDisplaysIntegrator extends HologramSubIntegrator {
    public HolographicDisplaysIntegrator() {
        super("HolographicDisplays", HolographicDisplaysHologram.class, "3.0.0-SNAPSHOT-b000", "SNAPSHOT-b");
    }

    @Override
    protected void init() throws HookException {
        super.init(); //Calling super validates version
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
        /* We must convert a normal BetonQuest variable such as "%objective.kills.left% to {bq:pack:objective.kills.left}
           which is parsed by HolographicDisplays as a custom API placeholder. */
        final Matcher matcher = HologramIntegrator.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            final String placeholder = group.startsWith("$") && group.endsWith("$") ? "{bqg:" : "{bq:";
            return placeholder + pack.getPackagePath() + ":" + group.replaceAll("(%|\\$)", "") + "}";
        });
    }
}
