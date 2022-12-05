package org.betonquest.betonquest.modules.config.quest;

import lombok.CustomLog;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.GlobalVariableID;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This {@link QuestPackageImpl} represents all functionality based on a {@link Quest}.
 */
@CustomLog
public class QuestPackageImpl extends QuestTemplate implements QuestPackage {
    /**
     * Creates a new {@link QuestPackage}.  For more information see {@link Quest}.
     *
     * @param questPath the path that addresses this {@link QuestPackage}
     * @param root      the root file of this {@link QuestPackage}
     * @param files     all files contained by this {@link QuestPackage}
     * @throws InvalidConfigurationException thrown if a {@link QuestPackage} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public QuestPackageImpl(final String questPath, final File root, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        super(questPath, root, files);
    }

    @Override
    public boolean hasTemplate(final String templatePath) {
        return getTemplates().contains(templatePath);
    }

    @Override
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public String getRawString(final String address) {
        final String[] parts = address.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        final String path = parts[0];
        int startPath = 1;
        ConfigurationSection section = getConfig().getConfigurationSection(path);
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

    @Override
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.NcssCount"})
    public String subst(final String input) {
        if (input == null) {
            return null;
        }
        String variableInput = input;

        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        while (true) {
            final Matcher matcher = globalVariableRegex.matcher(variableInput);
            if (!matcher.find()) {
                break;
            }
            final String varName = matcher.group(1);
            final String varVal;
            if ("this".equals(varName)) {
                varVal = getQuestPath();
            } else {
                final GlobalVariableID variableID;
                try {
                    variableID = new GlobalVariableID(this, varName);
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(this, e.getMessage(), e);
                    return variableInput;
                }
                final String varRaw = variableID.getPackage().getConfig().getString("variables." + variableID.getBaseID());
                if (varRaw == null) {
                    LOG.warn(this, String.format("Variable %s not defined in package %s", variableID.getBaseID(), variableID.getPackage().getQuestPath()));
                    return variableInput;
                }
                varVal = variableID.getPackage().resolve(varRaw);
            }

            if (varVal
                    .matches("^\\$[a-zA-Z\\d]+\\$->\\(-?\\d+\\.?\\d*;-?\\d+\\.?\\d*;-?\\d+\\.?\\d*\\)$")) {
                final String innerVarName = varVal.substring(1, varVal.indexOf('$', 2));
                final String innerVarVal = getConfig().getString("variables." + innerVarName);
                if (innerVarVal == null) {
                    LOG.warn(this, String.format("Location variable %s is not defined, in variable %s, package %s.",
                            innerVarName, varName, getQuestPath()));
                    return variableInput;
                }

                if (!innerVarVal.matches("^-?\\d+;-?\\d+;-?\\d+;.+$")) {
                    LOG.warn(this,
                            String.format("Inner variable %s is not valid location, in variable %s, package %s.",
                                    innerVarName, varName, getQuestPath()));
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
                            innerVarName, varName, getQuestPath()), e);
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
                            varName, getQuestPath()), e);
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

    @Override
    public String resolve(final String input) {
        if (input == null) {
            return null;
        }
        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        final Matcher matcher = globalVariableRegex.matcher(input);
        final StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            final String varName = matcher.group(1);
            final String varVal;
            if ("this".equals(varName)) {
                varVal = getQuestPath();
            } else {
                try {
                    final GlobalVariableID variableID = new GlobalVariableID(this, varName);
                    varVal = "$" + variableID.getPackage().getQuestPath() + "." + variableID.getBaseID() + "$";
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(this, e.getMessage(), e);
                    return input;
                }
            }
            matcher.appendReplacement(builder, Matcher.quoteReplacement(varVal));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    @Override
    public String getString(final String address) {
        return getString(address, null);
    }

    @Override
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

    @Override
    public String getFormattedString(final String address) {
        return Utils.format(getString(address));
    }

}
