package org.betonquest.betonquest.api.bukkit.config.util;

@SuppressWarnings({"unused", "PMD.CommentRequired", "PMD.JUnit4TestShouldUseTestAnnotation", "PMD.TooManyMethods", "PMD.ExcessivePublicCount"})
public interface ConfigurationSectionTestInterface {
    void testGetKeysDeepFalse();

    void testGetKeysDeepTrue();

    void testGetValuesDeepFalse();

    void testGetValuesDeepTrue();

    void testContains();

    void testContainsOnInvalidConfigPath();

    void testContainsIgnoreDefaultFalse();

    void testContainsIgnoreDefaultFalseOnInvalidConfigPath();

    void testContainsIgnoreDefaultTrue();

    void testContainsIgnoreDefaultTrueOnInvalidConfigPath();

    void testContainsOnDefault();

    void testContainsOnDefaultOnInvalidConfigPath();

    void testContainsIgnoreDefaultFalseOnDefault();

    void testContainsIgnoreDefaultFalseOnDefaultOnInvalidConfigPath();

    void testContainsIgnoreDefaultTrueOnDefault();

    void testContainsIgnoreDefaultTrueOnDefaultOnInvalidConfigPath();

    void testIsSet();

    void testIsSetOnInvalidConfigPath();

    void testGetCurrentPath();

    void testGetName();

    void testGetRoot();

    void testGetParent();

    void testGet();

    void testGetOnInvalidConfigPath();

    void testGetWithDefault();

    void testGetWithDefaultOnInvalidConfigPath();

    void testSet();

    void testSetOnExistingConfigPath();

    void testCreateSection();

    void testCreateSectionOnExistingConfigPath();

    void testCreateSectionWithValues();

    void testCreateSectionWithValuesOnExistingConfigPath();

    void testGetString();

    void testGetStringOnInvalidConfigPath();

    void testGetStringWithDefault();

    void testGetStringWithDefaultOnInvalidConfigPath();

    void testIsString();

    void testIsStringOnInvalidConfigPath();

    void testGetInt();

    void testGetIntOnInvalidConfigPath();

    void testGetIntWithDefault();

    void testGetIntWithDefaultOnInvalidConfigPath();

    void testIsInt();

    void testIsIntOnInvalidConfigPath();

    void testGetBoolean();

    void testGetBooleanOnInvalidConfigPath();

    void testGetBooleanWithDefault();

    void testGetBooleanWithDefaultOnInvalidConfigPath();

    void testIsBoolean();

    void testIsBooleanOnInvalidConfigPath();

    void testGetDouble();

    void testGetDoubleOnInvalidConfigPath();

    void testGetDoubleWithDefault();

    void testGetDoubleWithDefaultOnInvalidConfigPath();

    void testIsDouble();

    void testIsDoubleOnInvalidConfigPath();

    void testGetLong();

    void testGetLongOnInvalidConfigPath();

    void testGetLongWithDefault();

    void testGetLongWithDefaultOnInvalidConfigPath();

    void testIsLong();

    void testIsLongOnInvalidConfigPath();

    void testGetList();

    void testGetListOnInvalidConfigPath();

    void testGetListWithDefault();

    void testGetListWithDefaultOnInvalidConfigPath();

    void testIsList();

    void testIsListOnInvalidConfigPath();

    void testGetStringList();

    void testGetStringListOnInvalidConfigPath();

    void testGetIntegerList();

    void testGetIntegerListOnInvalidConfigPath();

    void testGetBooleanList();

    void testGetBooleanListOnInvalidConfigPath();

    void testGetDoubleList();

    void testGetDoubleListOnInvalidConfigPath();

    void testGetFloatList();

    void testGetFloatListOnInvalidConfigPath();

    void testGetLongList();

    void testGetLongListOnInvalidConfigPath();

    void testGetByteList();

    void testGetByteListOnInvalidConfigPath();

    void testGetCharacterList();

    void testGetCharacterListOnInvalidConfigPath();

    void testGetShortList();

    void testGetShortListOnInvalidConfigPath();

    void testGetMapList();

    void testGetMapListOnInvalidConfigPath();

    void testGetObject();

    void testGetObjectOnInvalidConfigPath();

    void testGetObjectWithDefault();

    void testGetObjectWithDefaultOnInvalidConfigPath();

    void testGetSerializable();

    void testGetSerializableOnInvalidConfigPath();

    void testGetSerializableWithDefault();

    void testGetSerializableWithDefaultOnInvalidConfigPath();

    void testGetVector();

    void testGetVectorOnInvalidConfigPath();

    void testGetVectorWithDefault();

    void testGetVectorWithDefaultOnInvalidConfigPath();

    void testIsVector();

    void testIsVectorOnInvalidConfigPath();

    void testGetOfflinePlayer();

    void testGetOfflinePlayerOnInvalidConfigPath();

    void testGetOfflinePlayerWithDefault();

    void testGetOfflinePlayerWithDefaultOnInvalidConfigPath();

    void testIsOfflinePlayer();

    void testIsOfflinePlayerOnInvalidConfigPath();

    void testGetItemStack();

    void testGetItemStackOnInvalidConfigPath();

    void testGetItemStackWithDefault();

    void testGetItemStackWithDefaultOnInvalidConfigPath();

    void testIsItemStack();

    void testIsItemStackOnInvalidConfigPath();

    void testGetColor();

    void testGetColorOnInvalidConfigPath();

    void testGetColorWithDefault();

    void testGetColorWithDefaultOnInvalidConfigPath();

    void testIsColor();

    void testIsColorOnInvalidConfigPath();

    void testGetLocation();

    void testGetLocationOnInvalidConfigPath();

    void testGetLocationWithDefault();

    void testGetLocationWithDefaultOnInvalidConfigPath();

    void testIsLocation();

    void testIsLocationOnInvalidConfigPath();

    void testGetConfigurationSection();

    void testGetConfigurationSectionOnInvalidConfigPath();

    void testIsConfigurationSection();

    void testIsConfigurationSectionOnInvalidConfigPath();

    void testGetDefaultSection();

    void testGetDefaultSectionOnInvalidConfigPath();

    void testAddDefaultOnChildSection();

    void testAddDefaultOnChildSectionOnExistingConfigPath();
}
