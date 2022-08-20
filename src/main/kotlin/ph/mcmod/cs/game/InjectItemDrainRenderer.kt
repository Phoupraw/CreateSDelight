package ph.mcmod.cs.game

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity
//TODO 烧烤翻面
interface InjectItemDrainRenderer {
    companion object {
        @JvmStatic
        fun modifyOffset(te: ItemDrainTileEntity, partialTicks: Float, value: Double): Double {
            return value
            te as InjectItemDrainTileEntity
            if (te.toastingStage == 0) {
                return value
            }
            val offset = 0.5 - value
            return when (te.toastingStage) {
                1 -> 0.5 - offset * 0.5
                in 2..5 -> 0.25 - (te.toastingStage - 2 + partialTicks) / 8
                6 -> offset * 0.5 - 0.5
                else -> value
            }
        }
        
        @JvmStatic
        fun modifyAngle(te: ItemDrainTileEntity, partialTicks: Float, offset: Float): Float {
            return offset
            te as InjectItemDrainTileEntity
//            if (te.toastingStage <= 1) {
//                return offset
//            }
            if (te.toastingStage in 2..5) {
                return offset + (te.toastingStage - 2 + partialTicks) / 8
            }
//            if (te.toastingStage >= 6) {
//                return offset
//            }
            return offset
        }
    }
}