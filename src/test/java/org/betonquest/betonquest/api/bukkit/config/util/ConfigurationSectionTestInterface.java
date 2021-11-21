package org.betonquest.betonquest.api.bukkit.config.util;

// Skipp cases where Mocking is necessary
public abstract interface ConfigurationSectionTestInterface {
    void testGetKeys();

    void testGetValues();

    void testContains();

    void testContainsOnInvalidConfigPath();

    void testContainsIgnoreDefault();

    void testContainsIgnoreDefaultOnInvalidConfigPath();

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

    void testGetColor();

    void testGetColorOnInvalidConfigPath();

    void testGetColorWithDefault();

    void testGetColorWithDefaultOnInvalidConfigPath();

    void testIsColor();

    void testIsColorOnInvalidConfigPath();

    void testGetConfigurationSection();

    void testGetConfigurationSectionOnInvalidConfigPath();

    void testIsConfigurationSection();

    void testIsConfigurationSectionOnInvalidConfigPath();

    void testGetDefaultSection();

    void testGetDefaultSectionOnInvalidConfigPath();

    void testAddDefault();

    void testAddDefaultOnExistingConfigPath();
}
