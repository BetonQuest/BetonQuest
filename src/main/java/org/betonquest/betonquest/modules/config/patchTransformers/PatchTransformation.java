package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public interface PatchTransformation {

    void transform(Map<String, String> options, ConfigurationSection config);
}
