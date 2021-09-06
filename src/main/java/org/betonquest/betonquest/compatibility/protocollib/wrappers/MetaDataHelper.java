package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class MetaDataHelper {


    private final WrappedDataWatcher.Serializer itemSerializer;
    private final WrappedDataWatcher.Serializer intSerializer;
    private final WrappedDataWatcher.Serializer byteSerializer;
    private final WrappedDataWatcher.Serializer booleanSerializer;
    private final WrappedDataWatcher.Serializer chatComponentSerializer;

    private final int entityStatusIndex;
    private final int customNameIndex;
    private final int customNameVisibleIndex;
    private final int noGravityIndex;
    private final int silentIndex;


    public MetaDataHelper() {
        entityStatusIndex = 0;
        customNameIndex = 2;
        customNameVisibleIndex = 3;
        noGravityIndex = 5;
        silentIndex = 4;
        itemSerializer = WrappedDataWatcher.Registry.get(MinecraftReflection.getItemStackClass());
        intSerializer = WrappedDataWatcher.Registry.get(Integer.class);
        byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
        chatComponentSerializer = WrappedDataWatcher.Registry.get(MinecraftReflection.getIChatBaseComponentClass(), true);

    }


    public void setEntityStatus(WrappedDataWatcher dataWatcher, byte statusBitmask) {
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(entityStatusIndex, byteSerializer), statusBitmask);
    }

    public WrappedWatchableObject getCustomNameWacthableObject(WrappedDataWatcher metadata) {
        return metadata.getWatchableObject(customNameIndex);
    }

    public void setCustomNameVisible(WrappedDataWatcher dataWatcher, boolean customNameVisible) {
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(customNameVisibleIndex, booleanSerializer), customNameVisible);
    }

    public void setNoGravity(WrappedDataWatcher dataWatcher, boolean noGravity) {
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(noGravityIndex, booleanSerializer), noGravity);
    }

    public void setSilent(WrappedDataWatcher dataWatcher, boolean silent) {
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(silentIndex, booleanSerializer), silent);
    }
}

