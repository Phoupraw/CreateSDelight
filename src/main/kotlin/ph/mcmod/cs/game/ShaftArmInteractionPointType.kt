package ph.mcmod.cs.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPointType
import net.minecraft.block.BlockState
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ShaftArmInteractionPointType(id: Identifier?) : ArmInteractionPointType(id) {
    override fun canCreatePoint(level: World?, pos: BlockPos?, state: BlockState): Boolean {
        return state.isOf(AllBlocks.SHAFT.get())
    }
    
    override fun createPoint(level: World?, pos: BlockPos?, state: BlockState?): ArmInteractionPoint? {
        return ShaftArmInteractionPoint(this, level, pos, state)
    }
}