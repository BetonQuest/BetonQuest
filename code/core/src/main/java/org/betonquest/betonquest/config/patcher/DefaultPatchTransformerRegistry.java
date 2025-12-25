package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegistry;
import org.betonquest.betonquest.config.patcher.transformer.KeyRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ListEntryAddTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ListEntryRemoveTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ListEntryRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformer.RemoveTransformer;
import org.betonquest.betonquest.config.patcher.transformer.SetTransformer;
import org.betonquest.betonquest.config.patcher.transformer.TypeTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ValueRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ValueReplaceTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link PatchTransformerRegistry} containing all transformers of BetonQuest.
 */
public class DefaultPatchTransformerRegistry implements PatchTransformerRegistry {

    /**
     * A map of transformers to use for patching and their names.
     */
    protected final Map<String, PatchTransformer> transformers;

    /**
     * Creates a new {@link DefaultPatchTransformerRegistry}.
     */
    public DefaultPatchTransformerRegistry() {
        transformers = new HashMap<>();
        transformers.put("SET", new SetTransformer());
        transformers.put("REMOVE", new RemoveTransformer());
        transformers.put("KEY_RENAME", new KeyRenameTransformer());
        transformers.put("VALUE_RENAME", new ValueRenameTransformer());
        transformers.put("VALUE_REPLACE", new ValueReplaceTransformer());
        transformers.put("LIST_ENTRY_ADD", new ListEntryAddTransformer());
        transformers.put("LIST_ENTRY_REMOVE", new ListEntryRemoveTransformer());
        transformers.put("LIST_ENTRY_RENAME", new ListEntryRenameTransformer());
        transformers.put("TYPE_TRANSFORM", new TypeTransformer());
    }

    @Override
    public Map<String, PatchTransformer> getTransformers() {
        return transformers;
    }
}
