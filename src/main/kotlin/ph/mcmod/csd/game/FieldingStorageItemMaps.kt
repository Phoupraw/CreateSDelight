package ph.mcmod.csd.game

import ph.mcmod.csd.storage.Storage_Item_Multiset
import java.util.*

interface FieldingStorageItemMaps {
    val storageItemMaps:MutableMap<UUID,Storage_Item_Multiset>
}