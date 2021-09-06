package org.betonquest.betonquest.compatibility.protocollib.wrappers;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityMetaDataHelper {
    private MetaDataHelper metadataHelper;

    public EntityMetaDataHelper(MetaDataHelper metadataHelper) {
        this.metadataHelper = metadataHelper;
    }

    public void setGlowing(Player reciever, Entity entity) {
        WrapperPlayServerEntityMetadata dataPacket = new WrapperPlayServerEntityMetadata();
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

        metadataHelper.setEntityStatus(dataWatcher, (byte) 0x40);

        dataPacket.setEntityID(entity.getEntityId());
        dataPacket.setEntityMetadata(dataWatcher.getWatchableObjects());
        dataPacket.sendPacket(reciever);

    }
}
