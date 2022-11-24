package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;
import java.util.regex.Matcher;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class DecentHologramsIntegrator extends HologramIntegrator {

    public DecentHologramsIntegrator() {
        super("DecentHolograms", "2.7.3");
    }

    @Override
    public BetonHologram createHologram(final String name, final Location location) {
        String hologramName = name;
        if (DHAPI.getHologram(hologramName) != null) {
            hologramName = name + UUID.randomUUID();
        }
        final Hologram hologram = DHAPI.createHologram(hologramName, location);
        hologram.enable();
        return new DecentHologramsHologram(hologram);
    }

    @Override
    public void hook() throws HookException {
        super.hook();
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            LOG.warn("Holograms from DecentHolograms will not be able to use BetonQuest variables in text lines " +
                    "without PlaceholderAPI plugin! Install it to use holograms with variables!");
        }
        final Version version = new Version(getPlugin().getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.compare(version, new Version("2.7.3")) != 0 && comparator.compare(version, new Version("2.7.4")) != 0) {
            LOG.warn("Holograms from DecentHolograms may not work correctly using versions of DecentHolograms other " +
                    "than 2.7.3 or 2.7.4. If you encounter any issues, please use either of those versions!");
        }
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack.objective.kills.left%" to
           "%betonquest_pack:objective.kills.left%" which is parsed by DecentHolograms as a PlaceholderAPI placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final Variable variable = BetonQuest.createVariable(pack, group);
                if (variable != null) {
                    final Instruction instruction = variable.getInstruction();
                    return "%betonquest_" + instruction.getPackage().getQuestPath() + ":" + instruction.getInstruction() + "%";
                }
            } catch (final InstructionParseException exception) {
                LOG.warn("Could not create variable '" + group + "' variable: " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
