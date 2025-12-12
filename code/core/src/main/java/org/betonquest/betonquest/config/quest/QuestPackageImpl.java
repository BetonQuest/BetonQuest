package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * This {@link QuestPackageImpl} represents all functionality based on a {@link Quest}.
 */
public class QuestPackageImpl extends QuestTemplate implements QuestPackage {

    /**
     * Creates a new {@link QuestPackage}.  For more information see {@link Quest}.
     *
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param questPath             the path that addresses this {@link QuestPackage}
     * @param root                  the root file of this {@link QuestPackage}
     * @param files                 all files contained by this {@link QuestPackage}
     * @throws InvalidConfigurationException thrown if a {@link QuestPackage} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public QuestPackageImpl(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final String questPath,
                            final File root, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        super(log, configAccessorFactory, questPath, root, files);
    }

    @Override
    public boolean hasTemplate(final String templatePath) {
        return getTemplates().contains(templatePath);
    }
}
