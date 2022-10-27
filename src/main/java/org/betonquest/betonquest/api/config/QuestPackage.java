package org.betonquest.betonquest.api.config;

import lombok.CustomLog;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.GlobalVariableID;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This {@link QuestPackage} represents a set of files that are merged together in a {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.AvoidDuplicateLiterals"})
@CustomLog
public class QuestPackage {
    @SuppressWarnings("PMD.CommentRequired")
    private final String packagePath;
    /**
     * The root package {@link ConfigAccessor} that represents this {@link QuestPackage}
     */
    private final ConfigAccessor packageConfig;
    /**
     * The list of all {@link ConfigAccessor}s of this {@link QuestPackage}
     */
    private final List<ConfigAccessor> configs;
    /**
     * The merged {@link MultiConfiguration} that represents this {@link QuestPackage}
     */
    private final MultiConfiguration config;

    /**
     * Creates a new {@link QuestPackage}. The {@code packagePath} represents the address of this {@link QuestPackage}.
     * The {@code packageConfig} is the root file of the {@link QuestPackage},
     * while the {@code files} are all other files except the {@code packageConfig} file.
     * <p>
     * All files are merged into one {@link MultiConfiguration} config.
     *
     * @param packagePath   the path that address this {@link QuestPackage}
     * @param packageConfig the file that represent the root of this {@link QuestPackage}
     * @param files         all files contained by this {@link QuestPackage} except the {@code packageConfig}
     * @throws InvalidConfigurationException thrown if a {@link ConfigAccessor} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public QuestPackage(final String packagePath, final File packageConfig, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        this.packagePath = packagePath;
        this.packageConfig = ConfigAccessor.create(packageConfig);
        this.configs = new ArrayList<>();

        final HashMap<ConfigurationSection, String> configurations = new HashMap<>();
        configurations.put(this.packageConfig.getConfig(), packageConfig.getParentFile().toURI().relativize(packageConfig.toURI()).getPath());
        for (final File file : files) {
            final ConfigAccessor configAccessor = ConfigAccessor.create(file);
            configs.add(configAccessor);
            final String filePath = packageConfig.getParentFile().toURI().relativize(configAccessor.getConfigurationFile().toURI()).getPath();
            configurations.put(configAccessor.getConfig(), filePath);
        }
        try {
            config = new MultiSectionConfiguration(new ArrayList<>(configurations.keySet()));
        } catch (final KeyConflictException e) {
            throw new InvalidConfigurationException(e.resolvedMessage(configurations), e);
        }
    }

    /**
     * Gets the path that address this {@link QuestPackage}.
     *
     * @return the address
     */
    public String getPackagePath() {
        return packagePath;
    }

    /**
     * Gets the merged {@link MultiConfiguration} that represents this {@link QuestPackage}
     *
     * @return the {@link MultiConfiguration}
     */
    public MultiConfiguration getConfig() {
        return config;
    }

    @SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
    public String getRawString(final String address) {
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        final String path = parts[0];
        int startPath = 1;
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null && path.equals("conversations")) {
            if (parts.length < 3) {
                return null;
            }
            section = section.getConfigurationSection(parts[1]);
            startPath = 2;
        }
        if (section == null) {
            return null;
        }
        final StringBuilder restPath = new StringBuilder();
        for (int i = startPath; i < parts.length; i++) {
            restPath.append(parts[i]);
            if (i < parts.length - 1) {
                restPath.append('.');
            }
        }
        return section.getString(restPath.toString(), null);
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CommentRequired", "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.NcssCount"})
    public String subst(final String input) {
        if (input == null) {
            return null;
        }
        String variableInput = input.replace("$this$", packagePath);

        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        while (true) {
            final Matcher matcher = globalVariableRegex.matcher(variableInput);
            if (!matcher.find()) {
                break;
            }
            final String varName = matcher.group(1);
            final String varVal;
            try {
                final GlobalVariableID variableID = new GlobalVariableID(this, varName);
                varVal = variableID.getPackage().getConfig().getString("variables." + variableID.getBaseID());
            } catch (final ObjectNotFoundException e) {
                LOG.warn(this, e.getMessage(), e);
                return variableInput;
            }
            if (varVal == null) {
                LOG.warn(this, String.format("Variable %s not defined in package %s", varName, packagePath));
                return variableInput;
            }

            if (varVal
                    .matches("^\\$[a-zA-Z0-9]+\\$->\\(-?\\d+\\.?\\d*;-?\\d+\\.?\\d*;-?\\d+\\.?\\d*\\)$")) {
                final String innerVarName = varVal.substring(1, varVal.indexOf('$', 2));
                final String innerVarVal = packageConfig.getConfig().getString("variables." + innerVarName);
                if (innerVarVal == null) {
                    LOG.warn(this, String.format("Location variable %s is not defined, in variable %s, package %s.",
                            innerVarName, varName, packagePath));
                    return variableInput;
                }

                if (!innerVarVal.matches("^-?\\d+;-?\\d+;-?\\d+;.+$")) {
                    LOG.warn(this,
                            String.format("Inner variable %s is not valid location, in variable %s, package %s.",
                                    innerVarName, varName, packagePath));
                    return variableInput;
                }

                final double locX;
                final double locY;
                final double locZ;
                final String rest;
                try {
                    final int offset1 = innerVarVal.indexOf(';');
                    locX = Double.parseDouble(innerVarVal.substring(0, offset1));
                    final int offset2 = innerVarVal.indexOf(';', offset1 + 1);
                    locY = Double.parseDouble(innerVarVal.substring(offset1 + 1, offset2));
                    final int offset3 = innerVarVal.indexOf(';', offset2 + 1);
                    locZ = Double.parseDouble(innerVarVal.substring(offset2 + 1, offset3));
                    rest = innerVarVal.substring(offset3);
                } catch (final NumberFormatException e) {
                    LOG.warn(this, String.format(
                            "Could not parse coordinates in inner variable %s in variable %s in package %s",
                            innerVarName, varName, packagePath), e);
                    return variableInput;
                }
                final double vecLocX;
                final double vecLocY;
                final double vecLocZ;
                try {
                    final int offset1 = varVal.indexOf('(');
                    final int offset2 = varVal.indexOf(';');
                    final int offset3 = varVal.indexOf(';', offset2 + 1);
                    final int offset4 = varVal.indexOf(')');
                    vecLocX = Double.parseDouble(varVal.substring(offset1 + 1, offset2));
                    vecLocY = Double.parseDouble(varVal.substring(offset2 + 1, offset3));
                    vecLocZ = Double.parseDouble(varVal.substring(offset3 + 1, offset4));
                } catch (final NumberFormatException e) {
                    LOG.warn(this, String.format("Could not parse vector in location variable %s in package %s",
                            varName, packagePath), e);
                    return variableInput;
                }
                final double locationX = locX + vecLocX;
                final double locationY = locY + vecLocY;
                final double locationZ = locZ + vecLocZ;
                variableInput = variableInput.replace("$" + varName + "$", String.format(Locale.US, "%.2f;%.2f;%.2f%s", locationX, locationY, locationZ, rest));
            } else {
                variableInput = variableInput.replace("$" + varName + "$", varVal);
            }
        }

        return variableInput;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getString(final String address) {
        return getString(address, null);
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getString(final String address, final String def) {
        final String value = getRawString(address);
        if (value == null) {
            return def;
        }
        if (!value.contains("$")) {
            return value;
        }

        return subst(value);
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getFormattedString(final String address) {
        return Utils.format(getString(address));
    }

    /**
     * Checks if a given {@code path} is physically saved in the {@link QuestPackage}'s package {@link ConfigAccessor}.
     * This can be used to validate that a {@code path} is covered the packageConfig.
     *
     * @param path to check
     * @return true if the path is defined in the packageConfig
     */
    public boolean isFromPackageConfig(final String path) {
        try {
            return config.getSourceConfigurationSection(path).equals(packageConfig.getConfig());
        } catch (final InvalidConfigurationException e) {
            return false;
        }
    }

    /**
     * Tries to save all modifications in the {@link MultiConfiguration} to files.
     *
     * @return true, and only true if there are no unsaved changes
     * @throws IOException thrown if an exception was thrown by calling {@link ConfigAccessor#save()}
     *                     or {@link MultiConfiguration#getUnsavedConfigs()} returned a {@link ConfigurationSection},
     *                     that is not represented by this {@link QuestPackage}
     */
    public boolean saveAll() throws IOException {
        boolean exceptionOccurred = false;
        unsaved:
        for (final ConfigurationSection unsavedConfig : config.getUnsavedConfigs()) {
            for (final ConfigAccessor configAccessor : configs) {
                if (unsavedConfig.equals(configAccessor.getConfig())) {
                    try {
                        configAccessor.save();
                    } catch (final IOException e) {
                        LOG.warn("Could not save file '" + configAccessor.getConfigurationFile().getPath() + "'! Reason: " + e.getMessage(), e);
                        exceptionOccurred = true;
                    }
                    continue unsaved;
                }
            }
            LOG.warn("No related ConfigAccessor found for ConfigurationSection '" + unsavedConfig.getName() + "'!");
            exceptionOccurred = true;
        }
        if (exceptionOccurred) {
            throw new IOException("It was not possible to save everything to files in the QuestPackage '" + packagePath + "'!");
        }
        return config.needSave();
    }

    /**
     * Gets the existing {@link ConfigAccessor} for the {@code relativePath}.
     * If the {@link ConfigAccessor} for the {@code relativePath} does not exist, a new one is created.
     *
     * @param relativePath the relative path from the root of the package
     * @return the already existing or new created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if there was an exception creating the new {@link ConfigAccessor}
     * @throws FileNotFoundException         thrown if the file for the new {@link ConfigAccessor} could not be found
     */
    public ConfigAccessor getOrCreateConfigAccessor(final String relativePath) throws InvalidConfigurationException, FileNotFoundException {
        final File root = packageConfig.getConfigurationFile().getParentFile();
        if (root.toURI().relativize(packageConfig.getConfigurationFile().toURI()).getPath().equals(relativePath)) {
            return packageConfig;
        }
        for (final ConfigAccessor configAccessor : configs) {
            if (root.toURI().relativize(configAccessor.getConfigurationFile().toURI()).getPath().equals(relativePath)) {
                return configAccessor;
            }
        }
        return createConfigAccessor(relativePath, root);
    }

    @NotNull
    private ConfigAccessor createConfigAccessor(final String relativePath, final File root) throws InvalidConfigurationException, FileNotFoundException {
        final File newConfig = new File(root, relativePath);
        final File newConfigParent = newConfig.getParentFile();
        if (!newConfigParent.exists() && !newConfigParent.mkdirs()) {
            throw new InvalidConfigurationException("It was not possible to create the folders for the file '" + newConfig.getPath() + "'!");
        }
        try {
            if (!newConfig.createNewFile()) {
                throw new InvalidConfigurationException("It was not possible to create the file '" + newConfig.getPath() + "'!");
            }
        } catch (final IOException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
        final ConfigAccessor newAccessor = ConfigAccessor.create(newConfig);
        configs.add(newAccessor);
        return newAccessor;
    }
}
