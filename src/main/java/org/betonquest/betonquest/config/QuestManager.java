package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.config.quest.QuestTemplate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to load all {@link QuestTemplate}s and {@link QuestPackage}s from the root directory and apply all templates.
 */
@SuppressWarnings("PMD.DataClass")
public class QuestManager {
    /**
     * The character to separate {@link Quest} address parts.
     */
    public static final String PACKAGE_SEPARATOR = "-";

    /**
     * Name of the folder to place {@link QuestTemplate}s in.
     */
    public static final String QUEST_TEMPLATES_FOLDER = "QuestTemplates";

    /**
     * Name of the folder to place {@link QuestPackage}s in.
     */
    public static final String QUEST_PACKAGES_FOLDER = "QuestPackages";

    /**
     * The type name of the file that indicates a file for a quest in a quest folder.
     */
    public static final String FILE_TYPE_INDICATOR = ".yml";

    /**
     * The name of the file that indicates a quest folder.
     */
    public static final String FILE_NAME_INDICATOR = "package";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * All loaded {@link QuestPackage}s.
     */
    private final Map<String, QuestPackage> packages;

    /**
     * Loads all {@link QuestTemplate}s and {@link QuestPackage}s from the root directory and applies all templates.
     *
     * @param loggerFactory         logger factory to use
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param root                  The root directory where to create the root folders for templates and packages
     */
    public QuestManager(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final File root) {
        this.log = log;
        this.packages = new HashMap<>();

        final File templatesDir = new File(root, QUEST_TEMPLATES_FOLDER);
        final File packagesDir = new File(root, QUEST_PACKAGES_FOLDER);

        if (!(createFolderIfAbsent(templatesDir) && createFolderIfAbsent(packagesDir))) {
            return;
        }

        final Map<String, QuestTemplate> templates = new HashMap<>();
        try {
            searchForPackages(templatesDir, templatesDir, FILE_NAME_INDICATOR, FILE_TYPE_INDICATOR, (questPath, questFile, files) -> {
                final QuestTemplate quest = new QuestTemplate(loggerFactory.create(QuestTemplate.class), configAccessorFactory, questPath, questFile, files);
                templates.put(quest.getQuestPath(), quest);
            });
            searchForPackages(packagesDir, packagesDir, FILE_NAME_INDICATOR, FILE_TYPE_INDICATOR, (questPath, questFile, files) -> {
                final QuestPackageImpl quest = new QuestPackageImpl(loggerFactory.create(QuestPackageImpl.class), configAccessorFactory, questPath, questFile, files);
                try {
                    quest.applyQuestTemplates(templates);
                } catch (final InvalidConfigurationException e) {
                    log.warn("Error while loading QuestPackage '" + quest.getQuestPath() + "'! Reason: " + e.getMessage(), e);
                    return;
                }
                if (quest.getConfig().getBoolean("package.enabled", true)) {
                    packages.put(quest.getQuestPath(), quest);
                }
            });
        } catch (final IOException e) {
            log.error("Error while loading '" + packagesDir.getPath() + "'!", e);
        }
    }

    /**
     * @return a map of packages and their names
     */
    public Map<String, QuestPackage> getPackages() {
        return packages;
    }

    private boolean createFolderIfAbsent(final File file) {
        if (!file.exists() && !file.mkdir()) {
            log.error("It was not possible to create the folder '" + file.getPath() + "'!");
            return false;
        }
        return true;
    }

    private List<File> searchForPackages(final File root, final File file, final String fileNameIndicator, final String fileTypeIndicator, final QuestCreator creator) throws IOException {
        final File[] fileArray = checkAndGetFiles(file);
        final List<File> files = new ArrayList<>();
        final File questFile = searchQuestFile(root, fileNameIndicator, fileTypeIndicator, creator, fileArray, files);
        if (questFile != null) {
            files.add(questFile);
            createPackage(root, questFile.getParentFile(), files, creator);
            files.clear();
        }
        return files;
    }

    @Nullable
    private File searchQuestFile(final File root, final String fileNameIndicator, final String fileTypeIndicator, final QuestCreator creator, final File[] fileArray, final List<File> files) {
        File questFile = null;
        for (final File subFile : fileArray) {
            if (subFile.isDirectory()) {
                try {
                    files.addAll(searchForPackages(root, subFile, fileNameIndicator, fileTypeIndicator, creator));
                } catch (final IOException e) {
                    log.warn(e.getMessage(), e);
                }
            } else {
                if (!subFile.getName().endsWith(fileTypeIndicator)) {
                    continue;
                }
                if (subFile.getName().equals(fileNameIndicator + fileTypeIndicator)) {
                    questFile = subFile;
                } else {
                    files.add(subFile);
                }
            }
        }
        return questFile;
    }

    private File[] checkAndGetFiles(final File file) throws IOException {
        if (!file.isDirectory()) {
            throw new IOException("File '" + file.getPath() + "' is not a directory!");
        }
        final File[] files = file.listFiles();
        if (files == null) {
            throw new IOException("Invalid list of files for directory '" + file.getPath() + "'!");
        }
        return files;
    }

    private void createPackage(final File root, final File relativeRoot, final List<File> files, final QuestCreator creator) {
        final String questPath = root.toURI().relativize(relativeRoot.toURI())
                .toString().replace('/', ' ').trim().replaceAll(" ", PACKAGE_SEPARATOR);
        try {
            creator.create(questPath, relativeRoot, files);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.warn(root.getParentFile().getName() + " '" + questPath + "' could not be loaded, reason: " + e.getMessage(), e);
        }
    }

    /**
     * Simple interface to create and register a {@link Quest}.
     */
    private interface QuestCreator {
        /**
         * Creates and registers a {@link Quest}.
         *
         * @param questPath    The path to this {@link Quest}
         * @param relativeRoot the root file of this {@link Quest}
         * @param files        All files of this {@link Quest}
         * @throws InvalidConfigurationException thrown if a {@link Quest} could not be created
         *                                       or an exception occurred while creating the {@link MultiConfiguration}
         * @throws FileNotFoundException         thrown if a file could not be found during the creation
         *                                       of a {@link ConfigAccessor}
         */
        void create(String questPath, File relativeRoot, List<File> files) throws InvalidConfigurationException, FileNotFoundException;
    }
}
