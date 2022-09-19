package ph.mcmod.csd.game

import com.simibubi.create.foundation.item.SmartInventory
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity

interface InjectFluidTankTileEntity {
    val inventory: SmartInventory
    
    companion object {
        @JvmStatic
        fun newInventory(te: SyncedTileEntity): SmartInventory {
            return SmartInventory(9, te)
        }
    }
}