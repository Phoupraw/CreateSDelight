package ph.mcmod.cs.game

import ph.mcmod.cs.storage.Storage_Item_Multiset
import java.util.*

interface FieldingStorageItemMaps {
    val storageItemMaps:MutableMap<UUID,Storage_Item_Multiset>
}