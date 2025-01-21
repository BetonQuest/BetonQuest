package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Removes an entry from a list.
 */
@SuppressWarnings("PMD.PrematureDeclaration")
public class ListEntryRemoveTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public ListEntryRemoveTransformer() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");
        final String regex = options.get("entry");

        final List<String> list = config.getStringList(key);
        final boolean listExists = config.isList(key);

        final Pattern pattern = Pattern.compile(regex);
        final boolean modified = list.removeIf(entry -> {
            final Matcher matcher = pattern.matcher(entry);
            return matcher.matches();
        });

        config.set(key, list);

        if (!listExists) {
            throw new PatchException("List '" + key + "' did not exist, so an empty list was created.");
        }
        if (!modified) {
            throw new PatchException("Tried to remove '%s' but there was no such element in the list '%s'.".formatted(regex, key));
        }
    }
}
